<?php
global $conn;
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once "db.inc.php";

$method = $_SERVER['REQUEST_METHOD'];
$input = json_decode(file_get_contents('php://input'), true);

try {
    if ($method === 'GET') {

        if (isset($_GET['mode']) && $_GET['mode'] === 'subject') {
            if (!isset($_GET['teacher_id'])) {
                throw new Exception("Missing teacher_id");
            }

            $teacher_id = intval($_GET['teacher_id']);

            $stmt = $conn->prepare("SELECT schedule_id FROM teacher WHERE user_id = ?");
            $stmt->bind_param("i", $teacher_id);
            $stmt->execute();
            $result = $stmt->get_result();
            if ($result->num_rows === 0) throw new Exception("Teacher not found");

            $schedule_id = $result->fetch_assoc()['schedule_id'];

            $stmt = $conn->prepare("
                SELECT DISTINCT s.subject_id, s.title
                FROM schedule_subject ss
                JOIN subject s ON ss.subject_id = s.subject_id
                WHERE ss.schedule_id = ?
                ORDER BY s.title
            ");
            $stmt->bind_param("i", $schedule_id);
            $stmt->execute();
            $result = $stmt->get_result();

            $subjects = [];
            while ($row = $result->fetch_assoc()) {
                $subjects[] = [
                    "id" => $row["subject_id"],
                    "label" => $row["title"]
                ];
            }

            echo json_encode($subjects);
            return;
        }

        if (isset($_GET['mode']) && $_GET['mode'] === 'find' && isset($_GET['id'])) {
            $id = intval($_GET['id']);
            $stmt = $conn->prepare("SELECT * FROM assignment WHERE assignment_id = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            echo json_encode($stmt->get_result()->fetch_assoc());
            return;
        }

        // Default: return all assignments
        $result = $conn->query("SELECT * FROM assignment");
        echo json_encode($result ? $result->fetch_all(MYSQLI_ASSOC) : []);
    }

    elseif ($method === 'POST') {
        if (!isset($input['mode'])) throw new Exception("Missing mode");

        if ($input['mode'] === 'add') {
            foreach (['title', 'details', 'subject', 'deadline', 'percentage'] as $key) {
                if (empty($input[$key])) throw new Exception("Missing field: $key");
            }

            $title = $input['title'];
            $details = $input['details'];
            $subject_id = intval($input['subject']);
            $deadline = $input['deadline'];
            $percentage = floatval($input['percentage']);
            $fileData = $input['file_data'] ?? null;
            $fileName = $input['file_name'] ?? null;

            if ($fileData && $fileName) {
                if (!is_writable(__DIR__ . "/uploads/")) {
                    throw new Exception("Uploads directory is not writable");
                }
                $uploadDir = __DIR__ . "/uploads/";
                if (!is_dir($uploadDir)) mkdir($uploadDir, 0755, true);
                file_put_contents($uploadDir . basename($fileName), base64_decode($fileData));
            }

            $startDate = isset($input['start_date']) ? $input['start_date'] : date('Y-m-d');

            $stmt = $conn->prepare("
                INSERT INTO assignment (subject_id, title, details, start_date, end_date, percentage_of_grade)
                VALUES (?, ?, ?, ?, ?, ?)
            ");
            $stmt->bind_param("issssd", $subject_id, $title, $details, $startDate, $deadline, $percentage);
            $stmt->execute();

            echo json_encode(['success' => true, 'message' => 'Assignment added', 'assignment_id' => $conn->insert_id]);
            return;
        }

        if ($input['mode'] === 'update') {
            foreach (['id', 'title', 'details', 'subject', 'deadline', 'percentage'] as $key) {
                if (!isset($input[$key])) throw new Exception("Missing field: $key");
            }

            $id = intval($input['id']);
            $title = $input['title'];
            $details = $input['details'];
            $subject_id = intval($input['subject']);
            $deadline = $input['deadline'];
            $percentage = floatval($input['percentage']);

            $stmt = $conn->prepare("
                UPDATE assignment
                SET subject_id = ?, title = ?, details = ?, end_date = ?, percentage_of_grade = ?
                WHERE assignment_id = ?
            ");
            $stmt->bind_param("isssdi", $subject_id, $title, $details, $deadline, $percentage, $id);
            $stmt->execute();

            echo json_encode(["success" => true, "message" => "Assignment updated"]);
            return;
        }

        if ($input['mode'] === 'delete') {
            if (!isset($input['id'])) throw new Exception("Missing assignment ID for deletion");
            $id = intval($input['id']);
            $stmt = $conn->prepare("DELETE FROM assignment WHERE assignment_id = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            echo json_encode(["success" => true, "message" => "Assignment deleted"]);
            return;
        }
    }

    else {
        http_response_code(405);
        echo json_encode(["error" => "Method not allowed"]);
    }
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "error" => $e->getMessage()]);
}
