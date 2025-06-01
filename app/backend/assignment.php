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

        if (isset($_GET['mode']) && $_GET['mode'] === 'class_subject') {
            if (!isset($_GET['teacher_id'])) {
                throw new Exception("Missing teacher_id in request");
            }

            $teacher_id = intval($_GET['teacher_id']);

            // Get the teacher's schedule_id
            $stmt = $conn->prepare("SELECT schedule_id FROM teacher WHERE user_id = ?");
            $stmt->bind_param("i", $teacher_id);
            $stmt->execute();
            $result = $stmt->get_result();

            if ($result->num_rows === 0) {
                throw new Exception("Teacher not found");
            }

            $schedule_id = $result->fetch_assoc()['schedule_id'];

            // Find subjects in schedule_subject tied to the teacher
            $sql = "
                SELECT DISTINCT c.class_name, s.title
                FROM schedule_subject ss
                JOIN subject s ON ss.subject_id = s.subject_id
                JOIN class c ON s.class_id = c.class_id
                WHERE ss.schedule_id = ?
                ORDER BY c.class_name, s.title
            ";

            $stmt = $conn->prepare($sql);
            $stmt->bind_param("i", $schedule_id);
            $stmt->execute();
            $result = $stmt->get_result();

            $pairs = [];
            while ($row = $result->fetch_assoc()) {
                $pairs[] = [
                    "class" => $row["class_name"],
                    "subject" => $row["title"],
                    "label" => $row["class_name"] . " - " . $row["title"]
                ];
            }

            echo json_encode($pairs);
            return;
        }

        // Default: Get all assignments
        $sql = "SELECT * FROM assignment";
        $result = $conn->query($sql);
        $assignments = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
        echo json_encode($assignments);
    }

    elseif ($method === 'POST') {
        foreach (['title', 'details', 'class', 'subject', 'deadline', 'percentage'] as $key) {
            if (empty($input[$key])) {
                throw new Exception("Missing field: $key");
            }
        }

        $title = $input['title'];
        $details = $input['details'];
        $className = $input['class'];
        $subjectTitle = $input['subject'];
        $deadline = $input['deadline'];
        $percentage = floatval($input['percentage']);
        $fileData = $input['file_data'] ?? null;
        $fileName = $input['file_name'] ?? null;

        // Get class_id
        $stmt = $conn->prepare("SELECT class_id FROM class WHERE class_name = ?");
        $stmt->bind_param("s", $className);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows === 0) {
            throw new Exception("Class not found: $className");
        }
        $class_id = $result->fetch_assoc()['class_id'];

        // Get subject_id for that class
        $stmt = $conn->prepare("SELECT subject_id FROM subject WHERE class_id = ? AND title = ?");
        $stmt->bind_param("is", $class_id, $subjectTitle);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows === 0) {
            throw new Exception("Subject '$subjectTitle' not found in class '$className'");
        }
        $subject_id = $result->fetch_assoc()['subject_id'];

        // Save file if provided
        if ($fileData && $fileName) {
            $uploadDir = __DIR__ . "/uploads/";
            if (!is_dir($uploadDir)) {
                mkdir($uploadDir, 0755, true);
            }
            $safeFileName = basename($fileName);
            $filePath = $uploadDir . $safeFileName;
            file_put_contents($filePath, base64_decode($fileData));
        }

        // Insert assignment
        $startDate = date('Y-m-d');
        $stmt = $conn->prepare(
            "INSERT INTO assignment (subject_id, title, start_date, end_date, percentage_of_grade) 
             VALUES (?, ?, ?, ?, ?)"
        );
        $stmt->bind_param("isssd", $subject_id, $title, $startDate, $deadline, $percentage);
        $stmt->execute();

        http_response_code(201);
        echo json_encode([
            'success' => true,
            'message' => 'Assignment added',
            'assignment_id' => $conn->insert_id
        ]);
    }

    else {
        http_response_code(405);
        echo json_encode(['error' => 'Method not allowed']);
    }

} catch (Exception $ex) {
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error' => $ex->getMessage()
    ]);
}
