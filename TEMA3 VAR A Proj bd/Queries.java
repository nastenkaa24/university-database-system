public class Queries {

    public static String studentsByYear(int year) {
        return """
                   select s.id_student,s.nume,s.prenume,s.data_nasterii
                   from STUDENTI s
                   where extract (year from data_nasterii)=2002
                   order by S.nume,s.prenume;
                """.formatted(year);
    }

    public static String coursesByMinCredits(int minCredits) {
        return """
                    select c.id_curs,c.denumire_curs
                    from cursuri c
                    where c.credit>=5
                    order by c.denumire_curs;
                """.formatted(minCredits);
    }

    public static final String joinStudentsCoursesCreditsGt4 = """
                select s.nume, s.prenume,c.denumire_curs,c.credit
                from studenti s
                join inrolari r on r.id_student=s.id_student
                join cursuri c on c.id_curs=r.id_curs
                where c.credit>4
                order by s.nume;
            """;

    public static final String leftJoinCoursesStudents = """
                select
                c.id_curs,c.denumire_curs,s.id_student,s.nume,s.prenume
                from Studenti s
                left join inrolari r on r.id_student=s.id_student
                left join cursuri c on c.id_curs=r.id_curs
                order by c.denumire_curs,s.nume;
            """;

    public static final String studentsWithoutCourseCreditsGt6 = """
               SELECT s.id_student, s.nume, s.prenume
               FROM studenti s
               WHERE NOT EXISTS (
               SELECT 1
               FROM inrolari i
               JOIN cursuri c ON c.id_curs = i.id_curs
               WHERE i.id_student = s.id_student
               AND c.credit > 6
               );
            """;

    public static String coursesWithStudentEmailDomainLike(String like) {
        if (like == null || like.trim().isEmpty()) like = "%@student.utcluj.ro";

        // pentru SQL: apostrof
        String safe = like.replace("'", "''");

        // pentru String.formatted: orice % din input trebuie dublat
        safe = safe.replace("%", "%%");

        return """
        SELECT c.id_curs, c.denumire_curs
        FROM cursuri c
        WHERE EXISTS (
            SELECT 1
            FROM inrolari i
            JOIN studenti s ON s.id_student = i.id_student
            WHERE i.id_curs = c.id_curs
              AND s.email LIKE '%s'
        );
        """.formatted(safe);
    }


    public static final String coursesCountPerSemester = """
               SELECT s.nume AS nume_student,s.prenume AS
               prenume_student,extract(month from i.data_inrolare) as
               semestru,COUNT(DISTINCT i.id_curs) AS nr_cursuri
               FROM studenti s
               left JOIN inrolari i ON i.id_student = s.id_student
               left JOIN cursuri c ON c.id_curs = i.id_curs
               GROUP BY s.nume, s.prenume, extract(month from
               i.data_inrolare)
               ORDER BY s.nume, s.prenume, extract(month from
               i.data_inrolare);
            """;

    public static final String totalCreditsPerCoursePassed = """
               select c.denumire_curs, SUM(case when i.nota_finala>=5
               then c.credit else 0 END) as total_credite
               from cursuri c
               left join inrolari i on i.id_curs=c.id_curs
               group by c.id_curs,c.denumire_curs
               order by denumire_curs;
            """;

    public static String studentsForCourse(int idCurs) {
        return "SELECT * FROM training.students_for_course(" + idCurs + ")";
    }

    public static String totalCoursesForStudent(int idStudent) {
        return "SELECT training.total_cursuri_student(" + idStudent + ")";
    }




}