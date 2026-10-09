package com.example.collab_desk.config;

import com.example.collab_desk.service.JwtService;
import com.example.collab_desk.service.RoomAccessService;
import com.example.collab_desk.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {
    private static final Pattern ROOM_TOPIC = Pattern.compile("^/topic/room\\.(\\d+)$");

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final RoomAccessService roomAccessService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (acc == null || acc.getCommand() == null) return message;

        switch (acc.getCommand()) {
            case CONNECT -> authenticate(acc);
            case SUBSCRIBE -> checkSubscribe(acc);
            case SEND -> checkSend(acc);
            default -> {
            }
        }

        return message;
    }

    private void authenticate(StompHeaderAccessor acc) {
        String header = acc.getFirstNativeHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new MessagingException("Missing token");
        }
        String token = header.substring(7);

        try {
            String email = jwtService.extractUsername(token);
            UserDetails user = userDetailsService.loadUserByUsername(email);
            if (!jwtService.isTokenValid(token, user)) {
                throw new MessagingException("Invalid token");
            }
            acc.setUser(new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()));
        } catch (MessagingException e) {
            throw e;
        } catch (Exception e) {
            throw new MessagingException("Invalid token");
        }
    }

    private void checkSubscribe(StompHeaderAccessor acc) {
        Principal principal = acc.getUser();
        if (principal == null) throw new MessagingException("Not authenticated");

        String dest = acc.getDestination();

        if ("/user/queue/notifications".equals(dest)) return;
        
        Matcher m = (dest == null) ? null : ROOM_TOPIC.matcher(dest);
        if (m == null || !m.matches()) {
            throw new MessagingException("Destination not allowed");
        }

        Long roomId = Long.valueOf(m.group(1));
        if (!roomAccessService.canAccess(principal.getName(), roomId)) {
            throw new MessagingException("Not a member of this room");
        }
    }

    private void checkSend(StompHeaderAccessor acc) {
        if (acc.getUser() == null) throw new MessagingException("Not authenticated");

        String dest = acc.getDestination();
        if (dest == null || !dest.startsWith("/app/")) {
            throw new MessagingException("Direct send to broker is not allowed");
        }
    }
}
