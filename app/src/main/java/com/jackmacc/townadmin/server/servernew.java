package com.jackmacc.townadmin.server;

class servernew {

    String new_title;
    String new_desc;
    String new_image;
    String new_time;
    String new_writer;
    String new_id;

    public servernew(String new_title, String new_image, String new_time, String new_id) {
        this.new_title = new_title;
        this.new_image = new_image;
        this.new_time = new_time;
        this.new_id = new_id;
    }

    public String getNew_title() {
        return new_title;
    }

    public void setNew_title(String new_title) {
        this.new_title = new_title;
    }

    public String getNew_desc() {
        return new_desc;
    }

    public void setNew_desc(String new_desc) {
        this.new_desc = new_desc;
    }

    public String getNew_image() {
        return new_image;
    }

    public void setNew_image(String new_image) {
        this.new_image = new_image;
    }

    public String getNew_time() {
        return new_time;
    }

    public void setNew_time(String new_time) {
        this.new_time = new_time;
    }

    public String getNew_writer() {
        return new_writer;
    }

    public void setNew_writer(String new_writer) {
        this.new_writer = new_writer;
    }
}
