package com.mattharrah.gedcom4j;

import java.util.ArrayList;
import java.util.List;

public class Repository {
	public String name;
    public String recFileNumber;
    public String recIdNumber;
    public Address address;
    public List<Note> notes;
    public ChangeDate changeDate;
    public List<UserReference> userReference = new ArrayList<UserReference>();
}
