<?php
global $conn;
require_once("db.inc.php");
header("Content-Type: application/json");

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    $input = json_decode(file_get_contents("php://input"), true);

    if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    if (isset($_GET['mode']) && $_GET['mode'] === 'list' && isset($_GET['student_id'])) {
        $student_id = intval($_GET['student_id']);

        $sql = "SELECT e.*, s.title AS subject_title, c.class_name AS class_title
                FROM exam e
                JOIN subject s ON e.subject_id = s.subject_id
                JOIN class c ON s.class_id = c.class_id
                JOIN student st ON c.class_id = st.class_id
                WHERE st.user_id = ? AND e.date >= CURDATE()";

        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $student_id);
        $stmt->execute();
        $result = $stmt->get_result();

        $exams = [];
        while ($row = $result->fetch_assoc()) {
            $exams[] = $row;
        }

        echo json_encode($exams);
        exit;
    }

      // --- LIST EXAMS BY SUBJECT ---
    if (isset($_GET['mode']) && $_GET['mode'] === 'list_by_subject' && isset($_GET['subject_id'])) {
        $subject_id = intval($_GET['subject_id']);

        $stmt = $conn->prepare("SELECT * FROM exam WHERE subject_id = ?");
        $stmt->bind_param("i", $subject_id);
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

    if (empty($input['exam']) || empty($input['students'])) {
        echo json_encode(["success" => false, "message" => "Missing data"]);
        exit;
    }

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

    $stmt2 = $conn->prepare("INSERT INTO student_exam_result (student_id, exam_id, score) VALUES (?, ?, ?)");
    foreach ($input['students'] as $student) {
        $student_id = $student['student_id'];
        $mark = $student['mark'];
        $stmt2->bind_param("iid", $student_id, $exam_id, $mark);
        $stmt2->execute();
    }

    echo json_encode(["success" => true, "message" => "Exam and student marks inserted"]);
} catch (Exception $e) {
    echo json_encode(["success" => false, "error" => $e->getMessage()]);
}
?>
