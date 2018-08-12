package com.myapp.voiceapp.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.springframework.format.annotation.DateTimeFormat;


@Entity
public class Query extends AbstractEntity {


    private static final String START_DATE_FORMAT_PATTERN = "MM/dd/yyyy";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT
            = new SimpleDateFormat(START_DATE_FORMAT_PATTERN);

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "option is required")
    private String option_1;

    @NotBlank(message = "option is required")
    private String option_2;

    @NotNull(message = "Please enter a valid date")
    @DateTimeFormat(pattern = START_DATE_FORMAT_PATTERN)
    private Date startDate;

    private String userId;
    
    private int option_1_count;
    
    private int option_2_count;	

    @ManyToMany(cascade = CascadeType.ALL)
    private final List<Category> categories = new ArrayList<>();

    public Query(String title, String option_1, String option_2, Date startDate, String s) {}

    public Query(@NotBlank String title, 
                 @NotNull String option_1,
                 @NotNull String option_2,
                 @NotNull Date startDate)
    {
        if (title == null || title.length() == 0)
            throw new IllegalArgumentException("Title may not be blank");

        if (option_1 == null || option_1.length() == 0)
            throw new IllegalArgumentException("Options may not be null");

        if (option_2 == null || option_2.length() == 0)
            throw new IllegalArgumentException("Options may not be null");

        if (startDate == null)
            throw new IllegalArgumentException("Start date may not be null");

        this.title = title;
        this.option_1 = option_1;
        this.option_2=option_2;
        this.startDate = startDate;
    }

    public Query(String title, String option_1,String option_2 , Date startDate, List<Category> categories) {
        this(title, option_1,option_2, startDate);
        this.addAllCategories(categories);
    }
    public Query(){

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOption_1() {
        return option_1;
    }

    public void setOption_1(String option_1) {
        this.option_1 = option_1;
    }

    public String getOption_2() {
        return option_2;
    }

    public void setOption_2(String option_2) {
        this.option_2 = option_2;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getFormattedStartDate() {
        return Query.SIMPLE_DATE_FORMAT.format(startDate);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
 

	public int getOption_1_count() {
		return option_1_count;
	}

	public void setOption_1_count(int option_1_count) {
		this.option_1_count = option_1_count;
	}

	public int getOption_2_count() {
		return option_2_count;
	}

	public void setOption_2_count(int option_2_count) {
		this.option_2_count = option_2_count;
	}

	public List<Category> getCategories() {
        return this.categories;
    }

    public void addCategory(Category vol) {
        this.categories.add(vol);
    }

    public void addAllCategories(List<Category> vols) {
        this.categories.addAll(vols);
    }

    public String getCategoriesFormatted() {
        List<String> nameList = this.getCategories().stream().map(Category::getName).collect(Collectors.toList());
        return String.join(", ", nameList);
    }
}
