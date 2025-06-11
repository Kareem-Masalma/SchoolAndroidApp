<?php
require_once "db.inc.php";
global $conn;

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

$method = $_SERVER['REQUEST_METHOD'];


try {
    if ($method === 'GET') {
        if (isset($_GET['student_id']) && isset($_GET['subject_id'])) {
            $studentId = (int)$_GET['student_id'];
            $subjectId = (int)$_GET['subject_id'];

            $sql = "
                SELECT 
                    sar.student_id, 
                    a.assignment_id, 
                    a.title AS assignment_title,
                    s.title AS subject_title,
                    sar.score
                FROM student_assignment_result sar
                JOIN assignment a ON sar.assignment_id = a.assignment_id
                JOIN subject s ON a.subject_id = s.subject_id
                WHERE sar.student_id = ? AND a.subject_id = ?
            ";

            $stmt = $conn->prepare($sql);
            $stmt->bind_param("ii", $studentId, $subjectId);
            $stmt->execute();
            $result = $stmt->get_result();
            $rows = $result->fetch_all(MYSQLI_ASSOC);

            echo json_encode($rows);
        } else {
            http_response_code(400);
            echo json_encode(['error' => 'Missing student_id or subject_id']);
        }
    } else {
        http_response_code(405);
        echo json_encode(['error' => 'Method not allowed']);
    }
} catch (Exception $ex) {
    http_response_code(500);
    echo json_encode(['error' => $ex->getMessage()]);
}

?>