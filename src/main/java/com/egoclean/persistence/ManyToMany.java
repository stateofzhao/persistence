package com.egoclean.persistence;

/**
 * Establishes a many-to-many relation between two models
 *
 * @author cristian
 * @version 1.0
 */
public class ManyToMany {
    private final Class<?> mClassA;
    private final String mClassAPrimaryKey;
    private final Class<?> mClassB;
    private final String mClassBPrimaryKey;

    public ManyToMany(Class<?> classA, String classAPrimaryKey, Class<?> classB, String classBPrimaryKey) {
        mClassA = classA;
        mClassAPrimaryKey = classAPrimaryKey;
        mClassB = classB;
        mClassBPrimaryKey = classBPrimaryKey;
    }

    public ManyToMany(Class<?> classA, Class<?> classB) {
        mClassA = classA;
        mClassAPrimaryKey = SQLHelper.ID;
        mClassB = classB;
        mClassBPrimaryKey = SQLHelper.ID;
    }

    Class<?>[] getClasses() {
        Class<?>[] classes = new Class<?>[2];
        classes[0] = mClassA;
        classes[1] = mClassB;
        return classes;
    }

    /**
     * @return the SQL statement for the join table creation
     */
    String getCreateTableStatement() {
        StringBuilder builder = new StringBuilder();

        String classA = SqlUtils.normalize(mClassA.getSimpleName());
        String classB = SqlUtils.normalize(mClassB.getSimpleName());
        builder.append("CREATE TABLE IF NOT EXISTS ").append(getTableName(classA, classB));
        builder.append(" (").append(SQLHelper.PRIMARY_KEY).append(" AUTOINCREMENT, ");
        builder.append(classA).append("_").append(mClassAPrimaryKey).append(" INTEGER NOT NULL, ");
        builder.append(classB).append("_").append(mClassBPrimaryKey).append(" INTEGER NOT NULL");
        builder.append(");");

        return builder.toString();
    }

    /**
     * @param classA a model
     * @param classB another model
     * @return name of the joined class
     */
    static String getTableName(String classA, String classB) {
        if (classA.compareToIgnoreCase(classB) <= 0) {
            return new StringBuilder().append(classA).append("_").append(classB).toString();
        }
        return new StringBuilder().append(classB).append("_").append(classA).toString();
    }
}
