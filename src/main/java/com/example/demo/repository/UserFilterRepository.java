package com.example.demo.repository;

import com.example.demo.model.Account;
import com.example.demo.model.Email;
import com.example.demo.model.Phone;
import com.example.demo.model.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
@AllArgsConstructor
public class UserFilterRepository {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String JOINS = " left join phone ph on u.id = ph.user_id left join email e on u.id = e.user_id ";
    private static final String CONDITIONS = " left join phone ph on u.id = ph.user_id left join email e on u.id = e.user_id ";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional
    public Long getTotalCount(String dateOfBirth, String email, String phone, String name) {
        var rowCountSql = new StringBuilder("SELECT count(distinct u) AS row_count FROM users u");
        rowCountSql.append(CONDITIONS);
        var parameters = new HashMap<String, Object>();
        var condition = new ArrayList<String>();
        fillConditionsAndParameters(condition, parameters, dateOfBirth, email, phone, name);
        if (!condition.isEmpty()) {
            rowCountSql.append(" WHERE ")
                    .append(String.join(" and ", condition));
        }
        return jdbcTemplate.queryForObject(rowCountSql.toString(), parameters, (rs, rowNum) -> rs.getLong(1));
    }

    @Transactional
    public List<User> findUsersByFilter(String dateOfBirth, String email, String phone, String name, String order, int limit, long offset) {
        var sqlIds = new StringBuilder("SELECT distinct u.id FROM users u left join account a on u.id = a.user_id ")
                .append(CONDITIONS);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("order", order);
        parameters.put("limit", limit);
        parameters.put("offset", offset);
        var condition = new ArrayList<String>();
        fillConditionsAndParameters(condition, parameters, dateOfBirth, email, phone, name);
        if (!condition.isEmpty()) {
            sqlIds.append(" WHERE ")
                    .append(String.join(" and ", condition));
        }
        List<Long> listIds = jdbcTemplate.queryForList(sqlIds.toString(), parameters, Long.class);
        String sqlUsers = """
                SELECT u.*, a.id as aId, a.balance as aBalance, ph.id as phId, ph.phone as phPhone, e.id as eId, e.email as eEmail
                FROM users u left join account a on u.id = a.user_id""" + JOINS + """
                WHERE
                    u.id in (:listIds)
                    order by :order limit :limit offset :offset""";
        parameters.put("listIds", new HashSet<>(listIds));
        var rows = jdbcTemplate.queryForList(sqlUsers, parameters);
        return getUsers(rows);
    }

    private List<User> getUsers(List<Map<String, Object>> rows) {
        Map<Long, User> userMap = new HashMap<>();
        Set<Long> phones = new HashSet<>();
        Set<Long> emails = new HashSet<>();

        for (Map<String, Object> map : rows) {
            User result = userMap.get(map.get("id"));
            if (result == null) {
                result = new User();
                result.setId((Long) map.get("id"));
                result.setName((String) map.get("name"));
                result.setBirthDate(LocalDate.parse(map.get("date_of_birth").toString(), FORMATTER));
                result.setPassword((String) map.get("password"));
                result.setAccount(new Account());
                result.getAccount().setId((Long) map.get("aId"));
                result.getAccount().setBalance((BigDecimal) (map.get("aBalance")));
                result.setEmails(new ArrayList<>());
                result.setPhones(new ArrayList<>());
                userMap.put((Long) map.get("id"), result);
            }
            var phoneId = (Long) map.get("phId");
            if (!phones.contains(phoneId)) {
                var phone = new Phone();
                phone.setId(phoneId);
                phone.setPhone((String) map.get("phPhone"));
                result.getPhones().add(phone);
                phones.add(phoneId);
            }
            var emailId = (Long) map.get("eId");
            if (!emails.contains(emailId)) {
                var email = new Email();
                email.setId(emailId);
                email.setEmail((String) map.get("eEmail"));
                result.getEmails().add(email);
                emails.add(emailId);
            }
        }
        return new ArrayList<>(userMap.values());
    }

    private void fillConditionsAndParameters(ArrayList<String> condition, Map<String, Object> parameters, String dateOfBirth, String email, String phone, String name) {
        if (name != null) {
            condition.add("u.name = :name ");
            parameters.put("name", name);
        }
        if (dateOfBirth != null) {
            condition.add(" u.date_of_birth >= :dateOfBirth ");
            parameters.put("dateOfBirth", dateOfBirth);
        }
        if (phone != null) {
            condition.add(" ph.phone = :phone ");
            parameters.put("phone", phone);
        }
        if (email != null) {
            condition.add(" e.email like :email ");
            parameters.put("email", "%" + email + "%");
        }
    }

}
