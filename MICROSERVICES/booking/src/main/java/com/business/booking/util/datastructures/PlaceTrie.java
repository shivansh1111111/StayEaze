package com.business.booking.util.datastructures;

import com.business.booking.database.entities.Place;
import com.business.booking.dto.PlaceAutoSearchDto;
import com.business.booking.dto.PlaceDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// PlaceTrie.java
@Component
public class PlaceTrie {
    private final TrieNode root;

    public PlaceTrie() {
        root = new TrieNode();
    }

    // Insert a place into the Trie
    public void insert(PlaceAutoSearchDto placeDto) {
        String placeName = placeDto.getTitle().toLowerCase();
        TrieNode current = root;

        for (char ch : placeName.toCharArray()) {
            current = current.getChildren().computeIfAbsent(ch, c -> new TrieNode());
        }

        current.setEndOfWord(true);
        current.setPlace(placeDto);
    }

    // Search for all places with given prefix
    public List<PlaceAutoSearchDto> searchWithPrefix(String prefix) {
        List<PlaceAutoSearchDto> results = new ArrayList<>();

        if (prefix == null || prefix.isEmpty()) {
            return results;
        }

        prefix = prefix.toLowerCase();
        TrieNode current = root;

        // Navigate to the prefix node
        for (char ch : prefix.toCharArray()) {
            current = current.getChildren().get(ch);
            if (current == null) {
                return results; // Prefix not found
            }
        }

        // Collect all words with this prefix
        collectAllWords(current, results);
        return results;
    }

    // DFS to collect all words from a node
    private void collectAllWords(TrieNode node, List<PlaceAutoSearchDto> results) {
        if (node == null) {
            return;
        }

        if (node.isEndOfWord()) {
            PlaceAutoSearchDto dto = new PlaceAutoSearchDto();
            dto.setId(node.getPlace().getId());
            dto.setTitle(node.getPlace().getTitle());
            dto.setImageUrl(node.getPlace().getImageUrl());
            results.add(dto);
        }

        for (TrieNode child : node.getChildren().values()) {
            collectAllWords(child, results);
        }
    }

    // Clear the Trie
    public void clear() {
        root.getChildren().clear();
    }

}
