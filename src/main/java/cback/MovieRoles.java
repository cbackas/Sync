package cback;

public enum MovieRoles {
    STAFF("STAFF", "257995763399917569"),
    ADMIN("ADMIN", "256249078596370433"),
    MOD("MOD", "256249088830472193"),
    MOVIENIGHT("MOVIENIGHT", "226443478664609792");

    public String name;
    public String id;

    MovieRoles(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public static MovieRoles getRole(String name) {
        for (MovieRoles role : values()) {
            if (role.name.equalsIgnoreCase(name)) {
                return role;
            }
        }
        return null;
    }
}