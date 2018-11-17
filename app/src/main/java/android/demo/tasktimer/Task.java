package android.demo.tasktimer;

import java.io.Serializable;

class Task implements Serializable {
    public static final long serialVersionID = 20161120L;
    private long m_Id;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;


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
