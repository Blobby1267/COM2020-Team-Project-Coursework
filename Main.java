public class Main {
    static class User {
        private String username;
        private String points;
        
        // Constructor
        public User(String username, String points) {
            this.username = username;
            this.points = points;
        }
        
        // Getters
        public String getUsername() {
            return username;
        }
        public int getPoints() {
            return Integer.parseInt(points);
        }

        // Setter
        public void setUsername(String username) {
            this.username = username;
        }
        public void increasePoints(int increase) {
            this.points = points + increase;
        }
    }
    
    public static void main(String[] args) {
        User user1 = new User("Alice", "100");
        System.out.println("Username: " + user1.getUsername());
        System.out.println("Points: " + user1.getPoints());
    }
}
