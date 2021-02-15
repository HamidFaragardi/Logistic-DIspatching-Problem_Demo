package base;

public class Class1 implements Cloneable  {
    int a;
    public Class1 clone() throws
            CloneNotSupportedException
    {
        // Assign the shallow copy to new reference variable t
        Class1 t = (Class1)super.clone();

        // Create a new object for the field c
        // and assign it to shallow copy obtained,
        // to make it a deep copy
        return t;
    }
    public Class1(int a)
    {
        this.a = a;
    }
}
