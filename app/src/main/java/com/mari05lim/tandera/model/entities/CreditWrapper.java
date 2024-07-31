package com.mari05lim.tandera.model.entities;

import androidx.annotation.NonNull;

import com.google.common.base.Preconditions;

/**
 * DEV Mari05liM
 */
public class CreditWrapper implements Comparable<CreditWrapper> {

    private final PersonWrapper person;
    private final String job;
    private String department;
    private final int order;

    private static final int ORDER_DIRECTOR = 0;
    private static final int ORDER_WRITER = 1;
    private static final int ORDER_PRODUCER = 2;
    private static final int ORDER_PRODUCTION = 3;
    private static final int ORDER_EDITING = 4;
    private static final int ORDER_CAMERA = 5;
    private static final int ORDER_ART = 6;
    private static final int ORDER_SOUND = 7;

    public CreditWrapper(PersonWrapper person, String job, int order) {
        this.person = Preconditions.checkNotNull(person, "Person cannot be null");
        this.job = Preconditions.checkNotNull(job, "Job cannot be null");
        this.order = order;
    }

    public CreditWrapper(PersonWrapper person, String job, String department) {
        this.person = Preconditions.checkNotNull(person, "Person cannot be null");
        this.job = Preconditions.checkNotNull(job, "Job cannot be null");
        this.department = Preconditions.checkNotNull(department, "Department cannot be null");
        this.order = crewOrderByJob(this);
    }

    public PersonWrapper getPerson() {
        return person;
    }

    public String getJob() {
        return job;
    }

    public String getDepartment() {
        return department;
    }

    private int getOrder() {
        return order;
    }

    @Override
    public int compareTo(@NonNull CreditWrapper another) {
        int thisOrder = getOrder();
        int otherOrder = another.getOrder();

        if (thisOrder != otherOrder) {
            return thisOrder - otherOrder;
        } else
        return this.person.name.compareTo(another.person.name);
    }

    private static int crewOrderByJob(CreditWrapper crew) {
        if (crew.job.equals("Director")) {
            return ORDER_DIRECTOR;
        } else if (crew.department.equals("Writing")) {
            return ORDER_WRITER;
        } else if (crew.job.equals("Producer")) {
            return ORDER_PRODUCER;
        } else if (crew.department.equals("Production")) {
            return ORDER_PRODUCTION;
        } else if (crew.department.equals("Editing")) {
            return ORDER_EDITING;
        } else if (crew.department.equals("Camera")) {
            return ORDER_CAMERA;
        } else if (crew.department.equals("Art")) {
            return ORDER_ART;
        } else if (crew.department.equals("Sound")) {
            return ORDER_SOUND;
        }
        return 69;
    }

}