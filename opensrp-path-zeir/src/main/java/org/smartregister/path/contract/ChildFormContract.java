package org.smartregister.path.contract;
public interface ChildFormContract {
    interface View{}
    interface Presenter{
        void tearDown();
    }
    interface Interactor{
        void tearDown();
    }
    interface Model{}
}
