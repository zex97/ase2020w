package com.studyboard.dto;

import com.studyboard.model.Space;

import java.time.LocalDate;
import java.util.Objects;

public class SpaceDTO {

    private Long id;
    private String name;
    private UserDTO userDTO;
    private String description;
    private LocalDate creationDate;

    public SpaceDTO() {
    }


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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    public Space toSpace() {
        Space space = new Space();
        space.setId(this.id);
        space.setName(this.name);
        space.setUser(this.userDTO.toUser());
        space.setDescription(this.description);
        space.setCreationDate(this.creationDate);
        return space;
    }

    public static SpaceDTO of(Space space) {
        SpaceDTO spaceDTO = new SpaceDTO();
        spaceDTO.setId(space.getId());
        spaceDTO.setName(space.getName());
        spaceDTO.setUserDTO(UserDTO.of(space.getUser()));
        spaceDTO.setDescription(space.getDescription());
        spaceDTO.setCreationDate(space.getCreationDate());
        return spaceDTO;
    }

    @Override
    public String toString() {
        return "spaceDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                "userDTO=" + userDTO.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpaceDTO that = (SpaceDTO) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(userDTO, that.userDTO);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (userDTO != null ? userDTO.hashCode() : 0);
        return result;
    }
}
