package roomescape.member.domain;

import java.util.regex.Pattern;
import roomescape.global.exception.BadRequestException;

public class Member {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final Long id;
    private final String name;
    private final String email;
    private final String passwordHash;

    public Member(Long id, String name, String email, String passwordHash) {
        validateName(name);
        validateEmail(email);
        validatePasswordHash(passwordHash);
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public static Member create(String name, String email, String passwordHash) {
        return new Member(null, name, email, passwordHash);
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public boolean hasSamePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException(MemberErrorCode.INVALID_MEMBER);
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException(MemberErrorCode.INVALID_MEMBER);
        }
    }

    private static void validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new BadRequestException(MemberErrorCode.INVALID_MEMBER);
        }
    }
}
