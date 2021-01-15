package com.studyboard.dto;

import com.studyboard.model.Deck;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class DeckDTO {

    private Long id;
    private String name;
    private Integer size;
    private LocalDate creationDate;
    private LocalDateTime lastTimeUsed;
    private boolean favorite;
    private UserDTO userDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(LocalDateTime lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public Deck toDeck() {
        Deck deck = new Deck();
        deck.setId(this.id);
        deck.setName(this.name);
        deck.setSize(this.size);
        deck.setCreationDate(this.creationDate);
        deck.setLastTimeUsed(this.lastTimeUsed);
        deck.setFavorite(this.favorite);
        deck.setUser(this.userDTO.toUser());
        return deck;
    }

    public static DeckDTO of(Deck deck) {
        DeckDTO deckDTO = new DeckDTO();
        deckDTO.setId(deck.getId());
        deckDTO.setName(deck.getName());
        deckDTO.setSize(deck.getSize());
        deckDTO.setCreationDate(deck.getCreationDate());
        deckDTO.setLastTimeUsed(deck.getLastTimeUsed());
        deckDTO.setFavorite(deck.isFavorite());
        deckDTO.setUserDTO(UserDTO.of(deck.getUser()));
        return deckDTO;
    }

    @Override
    public String toString() {
        return "DeckDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", creationDate=" + creationDate +
                ", lastTimeUsed=" + lastTimeUsed +
                ", favorite=" + favorite +
                (userDTO == null ? ", userDTO=null" : ", userDTO=" + userDTO.toString()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeckDTO that = (DeckDTO) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(size, that.size)) return false;
        if (!Objects.equals(creationDate, that.creationDate)) return false;
        if (!Objects.equals(lastTimeUsed, that.lastTimeUsed)) return false;
        if (!Objects.equals(favorite, that.favorite)) return false;
        return Objects.equals(userDTO, that.userDTO);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (lastTimeUsed != null ? lastTimeUsed.hashCode() : 0);
        result = 31 * result + (userDTO != null ? userDTO.hashCode() : 0);
        return result;
    }
}
