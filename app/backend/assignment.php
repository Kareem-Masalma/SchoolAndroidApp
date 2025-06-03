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
            if (!isset($_GET['teacher_id'])) throw new Exception("Missing teacher_id in request");

            $teacher_id = intval($_GET['teacher_id']);
            $stmt = $conn->prepare("SELECT schedule_id FROM teacher WHERE user_id = ?");
            $stmt->bind_param("i", $teacher_id);
            $stmt->execute();
            $result = $stmt->get_result();

            if ($result->num_rows === 0) throw new Exception("Teacher not found");
            $schedule_id = $result->fetch_assoc()['schedule_id'];

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

        if (isset($_GET['mode']) && $_GET['mode'] === 'find' && isset($_GET['id'])) {
            $id = intval($_GET['id']);
            $stmt = $conn->prepare("SELECT * FROM assignment WHERE assignment_id = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            $result = $stmt->get_result();
            echo json_encode($result->fetch_assoc());
            return;
        }

        $sql = "SELECT * FROM assignment";
        $result = $conn->query($sql);
        $assignments = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
        echo json_encode($assignments);
    }

    elseif ($method === 'POST') {
        if (isset($input['mode']) && $input['mode'] === 'submit') {
            foreach (['class', 'assignment', 'details'] as $key) {
                if (empty($input[$key])) throw new Exception("Missing field: $key");
            }

            $class = $input['class'];
            $assignmentTitle = $input['assignment'];
            $details = $input['details'];
            $fileData = $input['file_data'] ?? null;
            $fileName = $input['file_name'] ?? null;

            $stmt = $conn->prepare("
                SELECT a.assignment_id, s.class_id
                FROM assignment a
                JOIN subject s ON a.subject_id = s.subject_id
                JOIN class c ON s.class_id = c.class_id
                WHERE a.title = ? AND c.class_name = ?
            ");
            $stmt->bind_param("ss", $assignmentTitle, $class);
            $stmt->execute();
            $result = $stmt->get_result();

            if ($result->num_rows === 0) throw new Exception("Assignment not found");

            $row = $result->fetch_assoc();
            $assignment_id = $row['assignment_id'];
            $class_id = $row['class_id'];

            if ($fileData && $fileName) {
                $uploadDir = __DIR__ . "/submissions/";
                if (!is_dir($uploadDir)) mkdir($uploadDir, 0755, true);
                file_put_contents($uploadDir . basename($fileName), base64_decode($fileData));
            }

            echo json_encode([
                "success" => true,
                "message" => "Assignment submitted successfully",
                "assignment_id" => $assignment_id
            ]);
            return;
        }

        foreach (['title', 'details', 'class', 'subject', 'deadline', 'percentage'] as $key) {
            if (empty($input[$key])) throw new Exception("Missing field: $key");
        }

        $title = $input['title'];
        $details = $input['details'];
        $className = $input['class'];
        $subjectTitle = $input['subject'];
        $deadline = $input['deadline'];
        $percentage = floatval($input['percentage']);
        $fileData = $input['file_data'] ?? null;
        $fileName = $input['file_name'] ?? null;

        $stmt = $conn->prepare("SELECT class_id FROM class WHERE class_name = ?");
        $stmt->bind_param("s", $className);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows === 0) throw new Exception("Class not found: $className");
        $class_id = $result->fetch_assoc()['class_id'];

        $stmt = $conn->prepare("SELECT subject_id FROM subject WHERE class_id = ? AND title = ?");
        $stmt->bind_param("is", $class_id, $subjectTitle);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows === 0) throw new Exception("Subject '$subjectTitle' not found in class '$className'");
        $subject_id = $result->fetch_assoc()['subject_id'];

        if ($fileData && $fileName) {
            $uploadDir = __DIR__ . "/uploads/";
            if (!is_dir($uploadDir)) mkdir($uploadDir, 0755, true);
            file_put_contents($uploadDir . basename($fileName), base64_decode($fileData));
        }

        $startDate = date('Y-m-d');
        $stmt = $conn->prepare("INSERT INTO assignment (subject_id, title, start_date, end_date, percentage_of_grade) VALUES (?, ?, ?, ?, ?)");
        $stmt->bind_param("isssd", $subject_id, $title, $startDate, $deadline, $percentage);
        $stmt->execute();

        http_response_code(201);
        echo json_encode([
            'success' => true,
            'message' => 'Assignment added',
            'assignment_id' => $conn->insert_id
        ]);
    }

    elseif ($method === 'PUT') {
        if (!isset($input['id'], $input['title'], $input['details'], $input['class'], $input['subject'], $input['deadline'], $input['percentage'])) {
            throw new Exception("Missing required fields for update");
        }

        $id = intval($input['id']);
        $title = $input['title'];
        $details = $input['details'];
        $className = $input['class'];
        $subjectTitle = $input['subject'];
        $deadline = $input['deadline'];
        $percentage = floatval($input['percentage']);

        $stmt = $conn->prepare("SELECT class_id FROM class WHERE class_name = ?");
        $stmt->bind_param("s", $className);
        $stmt->execute();
        $classResult = $stmt->get_result();
        if ($classResult->num_rows === 0) throw new Exception("Class not found");
        $class_id = $classResult->fetch_assoc()['class_id'];

        $stmt = $conn->prepare("SELECT subject_id FROM subject WHERE class_id = ? AND title = ?");
        $stmt->bind_param("is", $class_id, $subjectTitle);
        $stmt->execute();
        $subjectResult = $stmt->get_result();
        if ($subjectResult->num_rows === 0) throw new Exception("Subject not found");
        $subject_id = $subjectResult->fetch_assoc()['subject_id'];

        $stmt = $conn->prepare("UPDATE assignment SET subject_id=?, title=?, details=?, end_date=?, percentage_of_grade=? WHERE assignment_id=?");
        $stmt->bind_param("isssdi", $subject_id, $title, $details, $deadline, $percentage, $id);
        $stmt->execute();

        echo json_encode(["success" => true, "message" => "Assignment updated"]);
    }

    elseif ($method === 'DELETE' && isset($_GET['id'])) {
        $id = intval($_GET['id']);
        $stmt = $conn->prepare("DELETE FROM assignment WHERE assignment_id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        echo json_encode(["success" => true, "message" => "Assignment deleted"]);
    }

    else {
        http_response_code(405);
        echo json_encode(['error' => 'Method not allowed']);
    }

} catch (Exception $ex) {
    http_response_code(400);
    echo json_encode(['success' => false, 'error' => $ex->getMessage()]);
}
