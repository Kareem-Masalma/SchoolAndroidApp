package com.example.schoolapp.data_access;

public interface IClassDA {
    void getAllClasses(ClassDA.ClassListCallback callback);
    void getClassById(int id, ClassDA.SingleClassCallback cb);

}
