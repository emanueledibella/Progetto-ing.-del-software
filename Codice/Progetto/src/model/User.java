package model;

public class User {
    private String tipologiaUtente;
    private String email;
    private String hashPassword;
    private String OTPCode;

    public void setSession(String tipologiaUtente) {
        this.tipologiaUtente = tipologiaUtente;
    }
    public void destroySession() {}

    public String getTipologiaUtente() {
        return this.tipologiaUtente;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return this.email;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }
    public String getHashPassword() {
        return this.hashPassword;
    }

    public void setOTPCode(String OTPCode) {
        this.OTPCode = OTPCode;
    }
    public String getOTPCode() {
        return this.OTPCode;
    }
}
