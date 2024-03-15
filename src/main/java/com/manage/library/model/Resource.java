package com.manage.library.model;

public class Resource {

    private int id;
    private String name;
    private String url;
    private int typeId;
    private int topicId;
    private int parentId;

    public Resource(String name, String url, int typeId, int topicId) {
        this.name = name;
        this.url = url;
        this.typeId = typeId;
        this.topicId = topicId;
    }
    
    
    public Resource(String name,  int typeId, int parentId, String url) {
        this.name = name;
        this.url = url;
        this.typeId = typeId;
        this.parentId = parentId;
    }
    
    
    
    public Resource(int id, String name, String url, int typeId, int topicId, int parentId) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.typeId = typeId;
        this.topicId = topicId;
        this.parentId = parentId;
    }

    public Resource() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        StringBuilder pathBuilder = new StringBuilder();
        appendPathSegments(this, pathBuilder);
        return pathBuilder.toString();
    }

    private static void appendPathSegments(Resource resource, StringBuilder pathBuilder) {
        if (resource.parentId != 0) {
            // Nếu có parentId, đệ quy để xây dựng đường dẫn từ cha đến con
            Resource parentResource = getParentResource(resource.parentId);
            appendPathSegments(parentResource, pathBuilder);
        }

        pathBuilder.append(resource.name).append("/");
    }

    private static Resource getParentResource(int parentId) {
        // Gọi đến cơ sở dữ liệu hoặc nguồn dữ liệu khác để lấy thông tin của parent resource
        // Ở đây là một giả định, bạn cần điều chỉnh để phản ánh cách bạn lấy dữ liệu từ nguồn của mình
        // Ví dụ: return database.getResourceById(parentId);
        return new Resource(); // Giả sử có một phương thức tương ứng trong lớp DAO hoặc lớp xử lý dữ liệu
    }

    @Override
    public String toString() {
        return "Resource{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", typeId=" + typeId
                + ", topicId=" + topicId
                + ", parentId=" + parentId
                + '}';
    }
}
