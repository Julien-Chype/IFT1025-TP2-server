package server.models;

import java.io.Serializable;

/**
 * The type Course.
 */
public class Course implements Serializable {

    private String name;
    private String code;
    private String session;

    /**
     * Instantiates a new Course.
     *
     * @param name    the name
     * @param code    the code
     * @param session the session
     */
    public Course(String name, String code, String session) {
        this.name = name;
        this.code = code;
        this.session = session;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets session.
     *
     * @return the session
     */
    public String getSession() {
        return session;
    }

    /**
     * Sets session.
     *
     * @param session the session
     */
    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "Course{" +
                "name=" + name +
                ", code=" + code +
                ", session=" + session +
                '}';
    }
}
