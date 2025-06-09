<?php
global $conn;
require_once("db.inc.php");
header("Content-Type: application/json");
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    $input = json_decode(file_get_contents("php://input"), true);

    // GET: Fetch exams for a teacher
    if ($_SERVER['REQUEST_METHOD'] === 'GET') {
        if (isset($_GET['mode']) && $_GET['mode'] === 'list' && isset($_GET['teacher_id'])) {
            $teacher_id = intval($_GET['teacher_id']);

            $sql = "SELECT e.*, sb.title AS subject_title, c.class_name AS class_title
                    FROM teacher t
                    JOIN schedule s ON t.schedule_id = s.schedule_id
                    JOIN schedule_subject ss ON s.schedule_id = ss.schedule_id
                    JOIN subject sb ON ss.subject_id = sb.subject_id
                    JOIN class c ON sb.class_id = c.class_id
                    JOIN exam e ON sb.subject_id = e.subject_id
                    WHERE t.user_id = ?";

            $stmt = $conn->prepare($sql);
            $stmt->bind_param("i", $teacher_id);
            $stmt->execute();
            $result = $stmt->get_result();

            $exams = [];
            while ($row = $result->fetch_assoc()) {
                $exams[] = $row;
            }

            echo json_encode($exams);
            exit;
        }
    }

    // POST: Either insert exam + marks OR just marks
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        if (!empty($input['exam'])) {
            // Insert exam and get ID
            $exam = $input['exam'];
            $title = $exam['title'];
            $subject_id = $exam['subject_id'];
            $date = $exam['date'];
            $duration = $exam['duration'];
            $percentage = $exam['percentage'];

            $stmt = $conn->prepare("INSERT INTO exam (title, subject_id, date, duration, percentage_of_grade) VALUES (?, ?, ?, ?, ?)");
            $stmt->bind_param("sisid", $title, $subject_id, $date, $duration, $percentage);
            $stmt->execute();
            $exam_id = $conn->insert_id;
        } elseif (!empty($input['exam_id'])) {
            // Use existing exam ID
            $exam_id = intval($input['exam_id']);
        } else {
            echo json_encode(["success" => false, "message" => "Missing exam or exam_id"]);
            exit;
        }

        // Check for student results
        if (empty($input['students'])) {
            echo json_encode(["success" => false, "message" => "Missing students"]);
            exit;
        }

        // Insert student marks
        $stmt2 = $conn->prepare("INSERT INTO student_exam_result (student_id, exam_id, score) VALUES (?, ?, ?)");
        foreach ($input['students'] as $student) {
            $student_id = $student['student_id'];
            $mark = $student['mark'];
            $stmt2->bind_param("iid", $student_id, $exam_id, $mark);
            $stmt2->execute();
        }

        echo json_encode(["success" => true, "message" => "Student marks submitted"]);
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "error" => $e->getMessage()]);
}
