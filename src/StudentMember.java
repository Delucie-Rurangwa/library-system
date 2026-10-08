public class StudentMember extends Member {
    private String studentCardNumber;

    public StudentMember() {
        super();
    }

    public StudentMember(String memberId, String name, String email, String studentCardNumber) {
        super(memberId, name, email); // Calls parent constructor
        this.studentCardNumber = studentCardNumber;
    }

    public String getStudentCardNumber() {
        return studentCardNumber;
    }

    public void setStudentCardNumber(String studentCardNumber) {
        this.studentCardNumber = studentCardNumber;
    }

    @Override
    public double calculateLateFees(int daysOverdue) {
        if (daysOverdue <= 0) return 0.0;
        return daysOverdue * 1.00;
    }
}