public class PublisherMember extends Member {
    private String publisherCompany;

    public PublisherMember() {
        super();
    }

    public PublisherMember(String memberId, String name, String email, String publisherCompany) {
        super(memberId, name, email);
        this.publisherCompany = publisherCompany;
    }

    public String getPublisherCompany() {
        return publisherCompany;
    }
    public void setPublisherCompany(String publisherCompany) {
        this.publisherCompany = publisherCompany; }

    @Override
    public double calculateLateFees(int daysOverdue) {
        if (daysOverdue <= 0) return 0.0;
        return daysOverdue * 0.50;
    }
}