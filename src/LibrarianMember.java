public class LibrarianMember extends Member {
    private String employeeId;

    public LibrarianMember() {
        super();
    }

    public LibrarianMember(String memberId, String name, String email, String employeeId) {
        super(memberId, name, email);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    @Override
    public double calculateLateFees(int daysOverdue) {
        return 0.0;
    }
}