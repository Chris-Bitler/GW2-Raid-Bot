package me.cbitler.raidbot.raids;

/**
 * Store data related to a flex role
 * This can be any spec/role combination
 * @author Christopher Bitler
 */
public class FlexRole {
    String spec, role;

    /**
     * Create a new FlexRole object
     * @param spec The spec related to this flex role
     * @param role The role related to this flex role
     */
    public FlexRole(String spec, String role) {
        this.spec = spec;
        this.role = role;
    }

    /**
     * Get the specialization related to this flex role
     * @return The specialization related to this flex role
     */
    public String getSpec() {
        return spec;
    }

    /**
     * Set the specialization related to this flex role
     * @param spec The specialization related to this flex role
     */
    public void setSpec(String spec) {
        this.spec = spec;
    }

    /**
     * Get the role related to this flex role
     * @return The role related to this flex role
     */
    public String getRole() {
        return role;
    }

    /**
     * Set the role related to this flex role
     * @param role The role related to this flex role
     */
    public void setRole(String role) {
        this.role = role;
    }
}
