package android.demo.tasktimer;

import java.io.Serializable;

class Task implements Serializable {
    // serialVersionUID is used to prevent different versions of Java from generating different values
    // which will then prevent our objects from being de-serialized correctly
    public static final long serialVersionID = 20161120L;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;
    // create fields to store columns
    // Instances of Task reflect a row in the database.
    // If we allow the fields to be changed, we could end up with an object having different values to what stored in the corresponding database row.
    // To prevent that, we are not going to write setters for these fields. Since the values are not changed, we mark them as final.
    // When we save a new task, we won't know what its ID is until after it has been saved in the database. We do need to update the ID field for a new record when the database tells us what id is used.
    private long m_Id;


    public Task(long id, String Name, String Description, int SortOrder) {
        this.m_Id = id;
        this.mName = Name;
        this.mDescription = Description;
        this.mSortOrder = SortOrder;
    }

    public long getid() {
        return m_Id;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getSortOrder() {
        return mSortOrder;
    }

    public void setId(long id) {
        this.m_Id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "m_Id=" + m_Id +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSortOrder=" + mSortOrder +
                '}';
    }

}
