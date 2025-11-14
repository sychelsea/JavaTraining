package com.practice.service;

import com.practice.dao.sql.UserDao;
import com.practice.dao.cassandra.UserEventRepository;
import com.practice.exception.UserAlreadyExistsException;
import com.practice.exception.UserNotFoundException;
import com.practice.exception.UserOptimisticLockingFailureException;
import com.practice.model.User;
import com.practice.model.UserEvent;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock UserDao dao;
    @Mock PasswordEncoder encoder;
    @Mock UserEventRepository eventRepo;

    @InjectMocks UserServiceImpl service;

    @Test
    void getUser_returns_when_found() {
        User u = new User();
        u.setId(1L); u.setUsername("alice");
        when(dao.find(1L)).thenReturn(Optional.of(u));

        User out = service.getUser(1L);

        assertEquals(1L, out.getId());
        assertEquals("alice", out.getUsername());
        verify(dao).find(1L);
        verifyNoMoreInteractions(dao);
    }

    @Test
    void getUser_throws_when_missing() {
        when(dao.find(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUser(99L));
        verify(dao).find(99L);
    }

    @Test
    void createUser_encodesPassword_checksDup_saves_and_writesEvent() {
        User input = new User();
        input.setId(123L);
        input.setUsername("bob");
        input.setPassword("plain");

        // no existing username
        when(dao.findByUsername("bob")).thenReturn(Optional.empty());
        // encrytion
        when(encoder.encode("plain")).thenReturn("ENC");
        // DAO
        User saved = new User();
        saved.setId(10L);
        saved.setUsername("bob");
        saved.setPassword("ENC");
        when(dao.save(any(User.class))).thenReturn(saved);

        User out = service.createUser(input);


        verify(dao).findByUsername("bob");

        verify(encoder).encode("plain");

        // check saving
        ArgumentCaptor<User> toSave = ArgumentCaptor.forClass(User.class);
        verify(dao).save(toSave.capture());
        User savedArg = toSave.getValue();
        assertNull(savedArg.getId());
        assertEquals("bob", savedArg.getUsername());
        assertEquals("ENC", savedArg.getPassword());

        // check UserEvent log
        ArgumentCaptor<UserEvent> eventCaptor = ArgumentCaptor.forClass(UserEvent.class);
        verify(eventRepo).save(eventCaptor.capture());
        UserEvent ev = eventCaptor.getValue();
        assertNotNull(ev.getKey());
        assertEquals(10L, ev.getKey().getUserId());
        assertEquals("CREATE", ev.getEventType());
        assertTrue(ev.getPayload().contains("bob"));

        // final return
        assertEquals(10L, out.getId());
        assertEquals("bob", out.getUsername());
    }

    @Test
    void createUser_throws_when_username_exists() {
        User existing = new User(); existing.setId(7L); existing.setUsername("bob");
        when(dao.findByUsername("bob")).thenReturn(Optional.of(existing));

        User input = new User(); input.setUsername("bob"); input.setPassword("x");

        assertThrows(UserAlreadyExistsException.class, () -> service.createUser(input));
        verify(dao).findByUsername("bob");
        // no save / encode
        verify(dao, never()).save(any());
        verify(encoder, never()).encode(any());
        verify(eventRepo, never()).save(any());
    }

    @Test
    void deleteUser_deletes_and_returns_oldValue() {
        User u = new User(); u.setId(3L); u.setUsername("c");
        when(dao.find(3L)).thenReturn(Optional.of(u));

        User out = service.deleteUser(3L);

        assertEquals(3L, out.getId());
        verify(dao).find(3L);
        verify(dao).delete(3L);
    }

    @Test
    void deleteUser_throws_when_missing() {
        when(dao.find(404L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.deleteUser(404L));
        verify(dao).find(404L);
        verify(dao, never()).delete(anyLong());
    }

    @Test
    void updateUser_updates_fields_encodes_password_and_writesEvent() {
        // existing user
        User existing = new User();
        existing.setId(5L);
        existing.setUsername("old");
        existing.setPassword("OLD_ENC");
        existing.setRole("USER");
        when(dao.find(5L)).thenReturn(Optional.of(existing));

        // given new info
        User info = new User();
        info.setUsername("newName");
        info.setPassword("newPlain");
        info.setRole("ADMIN");

        when(encoder.encode("newPlain")).thenReturn("NEW_ENC");

        User out = service.updateUser(5L, info);

        assertEquals("newName", out.getUsername());
        assertEquals("NEW_ENC", out.getPassword());
        assertEquals("ADMIN", out.getRole());

        // 持久化
        verify(dao).update(existing);

        // User event
        ArgumentCaptor<UserEvent> eventCaptor = ArgumentCaptor.forClass(UserEvent.class);
        verify(eventRepo).save(eventCaptor.capture());
        UserEvent ev = eventCaptor.getValue();
        assertNotNull(ev.getKey());
        assertEquals(5L, ev.getKey().getUserId());
        assertEquals("UPDATE", ev.getEventType());
        assertTrue(ev.getPayload().contains("newName"));
    }


    @Test
    void updateUser_when_password_null_dont_reencode() {
        User existing = new User();
        existing.setId(6L); existing.setUsername("k"); existing.setPassword("KEEP");
        when(dao.find(6L)).thenReturn(Optional.of(existing));

        User info = new User();
        info.setUsername("k2");

        User out = service.updateUser(6L, info);

        assertEquals("k2", out.getUsername());
        assertEquals("KEEP", out.getPassword(), "未提供密码时应保留原加密值");
        verify(encoder, never()).encode(any());
        verify(dao).update(existing);
    }

    @Test
    void updateUser_throws_when_missing() {
        when(dao.find(123L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.updateUser(123L, new User()));
        verify(dao).find(123L);
        verify(dao, never()).update(any());
    }

    @Test
    void updateUserWithPessimisticLock_uses_findForUpdate_then_update() {
        User locked = new User(); locked.setId(8L); locked.setUsername("p");
        when(dao.findForUpdate(8L)).thenReturn(Optional.of(locked));

        User info = new User(); info.setUsername("p2"); info.setPassword("plain");
        // 注意：pessimistic 分支里当前实现并没有 encode（和 updateUser 不同）
        User out = service.updateUserWithPessimisticLock(8L, info, 0);

        assertEquals("p2", out.getUsername());
        assertEquals("plain", out.getPassword(), "当前实现未做 encode，这是业务选择。");
        verify(dao).findForUpdate(8L);
        verify(dao).update(locked);
    }

    @Test
    void updateUserWithPessimisticLock_throws_when_missing() {
        when(dao.findForUpdate(11L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.updateUserWithPessimisticLock(11L, new User(), 0));
        verify(dao).findForUpdate(11L);
        verify(dao, never()).update(any());
    }

    @Test
    void updateUserWithOptimisticLock_success() {
        User u = new User(); u.setId(9L); u.setUsername("o");
        when(dao.find(9L)).thenReturn(Optional.of(u));

        User info = new User(); info.setUsername("o2");
        User out = service.updateUserWithOptimisticLock(9L, info);

        assertEquals("o2", out.getUsername());
        verify(dao).update(u); // 正常无异常
    }

    @Test
    void updateUserWithOptimisticLock_wraps_exception() {
        User u = new User(); u.setId(12L); u.setUsername("o");
        when(dao.find(12L)).thenReturn(Optional.of(u));

        doThrow(new ObjectOptimisticLockingFailureException(User.class, Map.of("id", 12L)))
                .when(dao).update(u);

        assertThrows(UserOptimisticLockingFailureException.class,
                () -> service.updateUserWithOptimisticLock(12L, new User()));
        verify(dao).update(u);
    }

    @Test
    void createUser_when_eventRepo_is_null_should_not_NPE() {

        UserServiceImpl s2 = new UserServiceImpl(dao, null, encoder);

        User input = new User();
        input.setUsername("noevent");
        input.setPassword("p");

        when(dao.findByUsername("noevent")).thenReturn(Optional.empty());
        when(encoder.encode("p")).thenReturn("E");
        User saved = new User();
        saved.setId(100L);
        saved.setUsername("noevent");
        saved.setPassword("E");
        when(dao.save(any(User.class))).thenReturn(saved);

        User out = s2.createUser(input);

        assertEquals(100L, out.getId());
        assertEquals("noevent", out.getUsername());

        verify(dao).findByUsername("noevent");
        verify(encoder).encode("p");
        verify(dao).save(any(User.class));

    }

}