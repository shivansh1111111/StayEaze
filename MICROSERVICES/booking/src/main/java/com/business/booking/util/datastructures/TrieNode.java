package com.business.booking.util.datastructures;

import com.business.booking.database.entities.Place;
import com.business.booking.dto.PlaceAutoSearchDto;
import com.business.booking.dto.PlaceDto;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class TrieNode {
    private Map<Character, TrieNode> children;
    private boolean isEndOfWord;
    private PlaceAutoSearchDto place;

    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

}
