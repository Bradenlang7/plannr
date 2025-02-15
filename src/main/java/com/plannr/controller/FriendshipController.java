package com.plannr.controller;

import com.plannr.dto.BaseUserDTO;
import com.plannr.dto.CreateFriendDTO;
import com.plannr.entity.Friendship;
import com.plannr.enums.FriendshipStatusEnum;
import com.plannr.jwt.JwtUtil;
import com.plannr.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/friendships")
public class FriendshipController {
    private final FriendshipService friendshipService;
    private final JwtUtil jwtUtil;

    public FriendshipController(FriendshipService friendshipService, JwtUtil jwtUtil) {
        this.friendshipService = friendshipService;
        this.jwtUtil = jwtUtil;
    }

    // Creates a new friendship with a PENDING status
    @PostMapping
    public ResponseEntity<String> createFriendship(@RequestBody CreateFriendDTO createFriendDTO) {
        Friendship friendship = friendshipService.createFriendshipPending(createFriendDTO);
        System.out.println(friendship);
        return ResponseEntity.ok("Friendship created");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFriendship(@PathVariable Long id) {
        friendshipService.deleteFriendship(id);

        return ResponseEntity.ok("Deleted friendship ");
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BaseUserDTO>> getFriendshipsByUserId(
            @PathVariable FriendshipStatusEnum status,
            @RequestHeader("Authorization") String authHeader) {
        System.out.println("Calling get friendships by user id " + authHeader);
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        Long userId = Long.valueOf(jwtUtil.extractUserId(token));

        List<BaseUserDTO> usersFriends = friendshipService.getFriendsByUserIdAndStatus(userId, status);
        System.out.println("usersFriends.size() = " + usersFriends.size());
        return ResponseEntity.ok(usersFriends);
    }


}
