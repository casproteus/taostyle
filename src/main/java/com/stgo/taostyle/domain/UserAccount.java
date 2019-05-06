package com.stgo.taostyle.domain;

import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Version;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.stgo.taostyle.web.TaoDebug;
import com.stgo.taostyle.web.TaoEmail;
import com.stgo.taostyle.web.TaoUtil;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

@Entity
@Configurable
public class UserAccount {

    private String loginname;

    private String password;

    private String cel;

    private String tel;

    private String fax;

    private String email;

    private String companyname;

    private String address;

    private String city;

    private String postcode;

    private int credit;

    private int balance;

    private String description;

    private String securitylevel;

    @ManyToOne
    private Person person;

    private int recordStatus;

    /**
     * to make it stronger, we also tack the possibility that the pUserName maybe actually email. note: the username is
     * unique, because there's *{personId} in the end.
     * 
     * @param userName
     * @return
     */
    public static com.stgo.taostyle.domain.UserAccount findUserAccountByName(
            String userName) {
        TypedQuery<UserAccount> tQuery =
                entityManager()
                        .createQuery("SELECT o FROM UserAccount AS o WHERE UPPER(o.loginname) = UPPER(:username)",
                                UserAccount.class);
        tQuery = tQuery.setParameter("username", userName);
        List<UserAccount> tList = tQuery.getResultList();
        if (tList != null && tList.size() == 1)
            return tList.get(0);
        else if (TaoEmail.isValidEmail(userName)) {
            // if it's null or have more than 1 match.
            tQuery =
                    entityManager().createQuery("SELECT o FROM UserAccount AS o WHERE UPPER(o.email) = UPPER(:tname)",
                            UserAccount.class);
            tQuery = tQuery.setParameter("tname", userName);
            tList = tQuery.getResultList();
            if (tList != null && tList.size() >= 1)
                return tList.get(0);
        }
        return null;
    }

    public static com.stgo.taostyle.domain.UserAccount findUserAccountByNameAndPassword(
            String userName,
            String password) {
        TypedQuery<UserAccount> tQuery =
                entityManager()
                        .createQuery(
                                "SELECT o FROM UserAccount AS o WHERE UPPER(o.loginname) = UPPER(:username) and o.password = :password",
                                UserAccount.class);
        tQuery = tQuery.setParameter("username", userName);
        tQuery = tQuery.setParameter("password", password);
        List<UserAccount> tList = tQuery.getResultList();
        if (tList != null && tList.size() == 1)
            return tList.get(0);
        else if (TaoEmail.isValidEmail(userName)) {
            // if it's null or have more than 1 match.
            tQuery =
                    entityManager()
                            .createQuery(
                                    "SELECT o FROM UserAccount AS o WHERE UPPER(o.email) = UPPER(:email) and o.password = :password",
                                    UserAccount.class);
            tQuery = tQuery.setParameter("email", userName);
            tQuery = tQuery.setParameter("password", password);
            tList = tQuery.getResultList();
            if (tList != null && tList.size() >= 1)
                return tList.get(0);
        }
        return null;
    }

    public static com.stgo.taostyle.domain.UserAccount findUserAccountsByNameAndTell(
            String userName,
            String tell) {
        tell = TaoUtil.cleanUpTel(tell);

        TypedQuery<UserAccount> tQuery =
                entityManager()
                        .createQuery(
                                "SELECT o FROM UserAccount AS o WHERE UPPER(o.loginname) = UPPER(:username) and o.tel = :tel or o.cel = :cel",
                                UserAccount.class);
        tQuery = tQuery.setParameter("username", userName);
        tQuery = tQuery.setParameter("tel", tell);
        tQuery = tQuery.setParameter("cel", tell);
        UserAccount userAccount = null;
        try {
            List<UserAccount> list = tQuery.getResultList();
            userAccount = list.get(0);
            if (list.size() > 1) {
                UserAccount oldUser = null;
                for (UserAccount user : list) {
                    if (oldUser == null) {
                        oldUser = user;
                    } else if (!oldUser.equals(user)) {
                        TaoDebug.error("found duplicated and not toltally same user in db", user.loginname);
                    }
                }
            }
        } catch (Exception e) {
            // do nothing.
        }

        return userAccount;
    }

    // override this method, so when the useraccount displayed in dropdown box, only the login name will be displayed
    // instead of all fields.
    @Override
    public String toString() {
        return this.getLoginname(); // return ReflectionToStringBuilder.toString(this,
                                    // ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static List<UserAccount> findAllUserAccountsByPerson(
            Person person) {
        TypedQuery<UserAccount> tQuery =
                entityManager().createQuery("SELECT o FROM UserAccount o where o.person = :person ORDER BY o.id DESC",
                        UserAccount.class);
        tQuery.setParameter("person", person);
        return tQuery.getResultList();
    }

    public static List<UserAccount> findUserAccountEntriesByPerson(
            int firstResult,
            int maxResults,
            Person person) {
        TypedQuery<UserAccount> tQuery =
                entityManager().createQuery("SELECT o FROM UserAccount o where o.person = :person ORDER BY o.id DESC",
                        UserAccount.class);
        tQuery.setParameter("person", person);
        return tQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Transactional
    public static long countUserAccountsByPerson(
            Person person) {
        return findAllUserAccountsByPerson(person).size();
    }

    @Transactional
    public static List<UserAccount> findUserAccountEntriesByPerson(
            int firstResult,
            int maxResults,
            String sortFieldName,
            String sortOrder,
            Person person) {
        String jpaQuery = "SELECT o FROM UserAccount o where o.person = :person";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }

        TypedQuery<UserAccount> tQuery = entityManager().createQuery(jpaQuery, UserAccount.class);
        tQuery.setParameter("person", person);
        return tQuery.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    @Transactional
    public static List<UserAccount> findAllUserAccountsByPerson(
            String sortFieldName,
            String sortOrder,
            Person person) {
        String jpaQuery = "SELECT o FROM UserAccount o where o.person =:person";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<UserAccount> tQuery = entityManager().createQuery(jpaQuery, UserAccount.class);
        tQuery.setParameter("person", person);
        return tQuery.getResultList();
    }

    public String getLoginname() {
        return this.loginname;
    }

    public void setLoginname(
            String loginname) {
        this.loginname = loginname;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(
            String password) {
        this.password = password;
    }

    public String getCel() {
        return this.cel;
    }

    public void setCel(
            String cel) {
        this.cel = cel;
    }

    public String getTel() {
        return this.tel;
    }

    public void setTel(
            String tel) {
        this.tel = TaoUtil.cleanUpTel(tel);
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(
            String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(
            String email) {
        this.email = email;
    }

    public String getCompanyname() {
        return this.companyname;
    }

    public void setCompanyname(
            String companyname) {
        this.companyname = companyname;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(
            String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(
            String city) {
        this.city = city;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(
            String postcode) {
        this.postcode = postcode;
    }

    public int getCredit() {
        return this.credit;
    }

    public void setCredit(
            int credit) {
        this.credit = credit;
    }

    public int getBalance() {
        return this.balance;
    }

    public void setBalance(
            int balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(
            String description) {
        this.description = description;
    }

    public String getSecuritylevel() {
        return this.securitylevel;
    }

    public void setSecuritylevel(
            String securitylevel) {
        this.securitylevel = securitylevel;
    }

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(
            Person person) {
        this.person = person;
    }

    public int getRecordStatus() {
        return this.recordStatus;
    }

    public void setRecordStatus(
            int recordStatus) {
        this.recordStatus = recordStatus;
    }

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

	public Integer getVersion() {
        return this.version;
    }

	public void setVersion(Integer version) {
        this.version = version;
    }

	@PersistenceContext
    transient EntityManager entityManager;

	public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("loginname", "password", "cel", "tel", "fax", "email", "companyname", "address", "city", "postcode", "credit", "balance", "description", "securitylevel", "person", "recordStatus");

	public static final EntityManager entityManager() {
        EntityManager em = new UserAccount().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

	@Transactional
    public static long countUserAccounts() {
        return findAllUserAccounts().size();
    }

	@Transactional
    public static List<UserAccount> findAllUserAccounts() {
        return entityManager().createQuery("SELECT o FROM UserAccount o", UserAccount.class).getResultList();
    }

	@Transactional
    public static List<UserAccount> findAllUserAccounts(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserAccount o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserAccount.class).getResultList();
    }

	@Transactional
    public static UserAccount findUserAccount(Long id) {
        if (id == null) return null;
        return entityManager().find(UserAccount.class, id);
    }

	@Transactional
    public static List<UserAccount> findUserAccountEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM UserAccount o", UserAccount.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional
    public static List<UserAccount> findUserAccountEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM UserAccount o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, UserAccount.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

	@Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            UserAccount attached = UserAccount.findUserAccount(this.id);
            this.entityManager.remove(attached);
        }
    }

	@Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

	@Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

	@Transactional
    public UserAccount merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        UserAccount merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

	public String toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }

	public String toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }

	public static UserAccount fromJsonToUserAccount(String json) {
        return new JSONDeserializer<UserAccount>()
        .use(null, UserAccount.class).deserialize(json);
    }

	public static String toJsonArray(Collection<UserAccount> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }

	public static String toJsonArray(Collection<UserAccount> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }

	public static Collection<UserAccount> fromJsonArrayToUserAccounts(String json) {
        return new JSONDeserializer<List<UserAccount>>()
        .use("values", UserAccount.class).deserialize(json);
    }
}
