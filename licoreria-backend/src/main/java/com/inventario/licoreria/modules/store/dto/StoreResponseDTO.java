package com.inventario.licoreria.modules.store.dto;

public class StoreResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String address;
    private Long managerId;
    private String managerUsername;
    private boolean isExternal;

    public StoreResponseDTO(Long id, String name, String description, String address, Long managerId, String managerUsername) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.managerId = managerId;
        this.managerUsername = managerUsername;
        this.isExternal = false;
    }

    public StoreResponseDTO(Long id, String name, String description, String address, Long managerId, String managerUsername, boolean isExternal) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.managerId = managerId;
        this.managerUsername = managerUsername;
        this.isExternal = isExternal;
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
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getManagerUsername() {
        return managerUsername;
    }

    public void setManagerUsername(String managerUsername) {
        this.managerUsername = managerUsername;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }
}
