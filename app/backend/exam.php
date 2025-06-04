<?php
global $conn;
require_once("db.inc.php");
header("Content-Type: application/json");

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {
    $input = json_decode(file_get_contents("php://input"), true);

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
