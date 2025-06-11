public class UserSession {
    private String currentUser;
    private String currentRole;

    public void login(String user, String role) {
        this.currentUser = user;
        this.currentRole = role;
    }

    public void logout() {
        currentUser = null;
        currentRole = null;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public boolean isStudent() {
        return "student".equals(currentRole);
    }

    public boolean isOrganizer() {
        return "organizer".equals(currentRole);
    }
}
