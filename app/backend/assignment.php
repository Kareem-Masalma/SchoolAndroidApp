<?php
global $conn;

// Ensure the uploads directory exists
$uploadDir = __DIR__ . '/uploads';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0755, true);
}

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once "db.inc.php";

$method = $_SERVER['REQUEST_METHOD'];
$input = json_decode(file_get_contents('php://input'), true);

try {
    if ($method === 'GET') {

        // --- FIND SUBMISSION BY ID ---
        if (isset($_GET['mode']) && $_GET['mode'] === 'find_submission' && isset($_GET['id'])) {
            $id = intval($_GET['id']);
            $stmt = $conn->prepare("SELECT * FROM student_assignment_result WHERE id = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            echo json_encode($stmt->get_result()->fetch_assoc());
            return;
        }

        // --- GET ALL SUBMISSIONS ---
        if (isset($_GET['mode']) && $_GET['mode'] === 'all_submissions') {
            $result = $conn->query("SELECT * FROM student_assignment_result ORDER BY id DESC");
            echo json_encode($result ? $result->fetch_all(MYSQLI_ASSOC) : []);
            return;
        }

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
            $stmt = $conn->prepare(
                "SELECT DISTINCT s.subject_id, s.title
                 FROM schedule_subject ss
                 JOIN subject s ON ss.subject_id = s.subject_id
                 WHERE ss.schedule_id = ?
                 ORDER BY s.title"
            );
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

        if (isset($_GET['mode']) && $_GET['mode'] === 'all') {
            $studentId = isset($_GET['student_id']) ? intval($_GET['student_id']) : null;

            if ($studentId !== null) {
                $sql = "
                    SELECT a.*, s.title AS subject_title, c.class_name AS class_title
                    FROM assignment a
                    JOIN subject s ON a.subject_id = s.subject_id
                    JOIN class c ON s.class_id = c.class_id
                    JOIN student st ON st.class_id = c.class_id
                    WHERE st.user_id = $studentId
                      AND a.end_date >= CURDATE()
                    ORDER BY a.end_date ASC
                ";
            } else {
                $sql = "
                    SELECT a.*, s.title AS subject_title, c.class_name AS class_title
                    FROM assignment a
                    JOIN subject s ON a.subject_id = s.subject_id
                    JOIN class c ON s.class_id = c.class_id
                    WHERE a.end_date >= CURDATE()
                    ORDER BY a.end_date ASC
                ";
            }

            $result = $conn->query($sql);
            $assignments = [];
            while ($row = $result->fetch_assoc()) {
                $assignments[] = $row;
            }

            echo json_encode($assignments);
            return;
        }

    }

    // --- SUBMIT ASSIGNMENT ---
    if (isset($input['mode']) && $input['mode'] === 'submit') {
        foreach (['student_id', 'assignment_title', 'details'] as $key) {
            if (empty($input[$key])) throw new Exception("Missing field: $key");
        }

        $studentId = intval($input['student_id']);
        $assignmentTitle = $input['assignment_title'];
        $details = $input['details'];
        $fileData = $input['file_data'] ?? null;
        $fileName = $input['file_name'] ?? null;
        $filePath = null;

        if ($fileData && $fileName) {
    $safeName = basename($fileName);
    $extension = pathinfo($safeName, PATHINFO_EXTENSION);
    $baseName = pathinfo($safeName, PATHINFO_FILENAME);

    // Create unique filename with user_id
    $finalName = $baseName . "_" . $studentId;
    $counter = 0;

    do {
        $suffix = $counter === 0 ? "" : "_($counter)";
        $newFileName = $finalName . $suffix . "." . $extension;
        $destPath = "$uploadDir/$newFileName";
        $counter++;
    } while (file_exists($destPath));

    // Save file and return relative path
    file_put_contents($destPath, base64_decode($fileData));
    $filePath = "/uploads/$newFileName";
}


        $stmt = $conn->prepare("SELECT assignment_id FROM assignment WHERE title = ?");
        $stmt->bind_param("s", $assignmentTitle);
        $stmt->execute();
        $result = $stmt->get_result();
        if ($result->num_rows === 0) throw new Exception("Assignment not found");
        $assignmentId = $result->fetch_assoc()['assignment_id'];

        $stmt = $conn->prepare("INSERT INTO student_assignment_submission (student_id, assignment_id, file_path, details)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE file_path=VALUES(file_path), details=VALUES(details), submitted_at=NOW()");
        $stmt->bind_param("iiss", $studentId, $assignmentId, $filePath, $details);
        $stmt->execute();

        echo json_encode(["success"=>true, "message"=>"Submission recorded"]);
        return;
    }

    // --- UPDATE SUBMISSION ---
    if (isset($input['mode']) && $input['mode'] === 'update_submission') {
        foreach (['student_id', 'assignment_id', 'details'] as $key) {
            if (!isset($input[$key])) throw new Exception("Missing field: $key");
        }

        $studentId = intval($input['student_id']);
        $assignmentId = intval($input['assignment_id']);
        $details = $input['details'];
        $fileData = $input['file_data'] ?? null;
        $fileName = $input['file_name'] ?? null;
        $filePath = null;

        if ($fileData && $fileName) {
    $safeName = basename($fileName);
    $extension = pathinfo($safeName, PATHINFO_EXTENSION);
    $baseName = pathinfo($safeName, PATHINFO_FILENAME);

    // Create unique filename with user_id
    $finalName = $baseName . "_" . $studentId;
    $counter = 0;

    do {
        $suffix = $counter === 0 ? "" : "_($counter)";
        $newFileName = $finalName . $suffix . "." . $extension;
        $destPath = "$uploadDir/$newFileName";
        $counter++;
    } while (file_exists($destPath));

    // Save file and return relative path
    file_put_contents($destPath, base64_decode($fileData));
    $filePath = "/uploads/$newFileName";
}


        if ($filePath) {
            $stmt = $conn->prepare("UPDATE student_assignment_submission SET file_path=?, details=?, submitted_at=NOW() WHERE student_id=? AND assignment_id=?");
            $stmt->bind_param("ssii", $filePath, $details, $studentId, $assignmentId);
        } else {
            $stmt = $conn->prepare("UPDATE student_assignment_submission SET details=?, submitted_at=NOW() WHERE student_id=? AND assignment_id=?");
            $stmt->bind_param("sii", $details, $studentId, $assignmentId);
        }
        $stmt->execute();
        echo json_encode(["success"=>true, "message"=>"Submission updated"]);
        return;
    }

    // --- DELETE SUBMISSION ---
    if (isset($input['mode']) && $input['mode'] === 'delete_submission') {
        if (!isset($input['id'])) throw new Exception("Missing submission ID for deletion");
        $id = intval($input['id']);
        $stmt = $conn->prepare("DELETE FROM student_assignment_submission WHERE id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        echo json_encode(["success"=>true, "message"=>"Submission deleted"]);
        return;
    }

    if ($method === 'POST') {
        if (!isset($input['mode'])) throw new Exception("Missing mode");

        // --- ADD ASSIGNMENT ---
        if ($input['mode'] === 'add') {
            foreach (['title', 'details', 'subject', 'deadline', 'percentage'] as $key) {
                if (empty($input[$key])) throw new Exception("Missing field: $key");
            }
            $title = $input['title'];
            $details = $input['details'];
            $subject_id = intval($input['subject']);
            $deadline = $input['deadline'];
            $percentage = floatval($input['percentage']);

            $stmt = $conn->prepare("SELECT accumulated_percentage FROM subject WHERE subject_id = ?");
            $stmt->bind_param("i", $subject_id);
            $stmt->execute();
            $result = $stmt->get_result();
            if ($result->num_rows === 0) throw new Exception("Subject not found");
            $accumulated = floatval($result->fetch_assoc()['accumulated_percentage']);
            if ($accumulated + $percentage > 100) {
                throw new Exception("Cannot add assignment. Total accumulated percentage will exceed 100%.");
            }

            $fileData = $input['file_data'] ?? null;
            $fileName = $input['file_name'] ?? null;
            $filePath = null;
            if ($fileData && $fileName) {
    $safeName = basename($fileName);
    $extension = pathinfo($safeName, PATHINFO_EXTENSION);
    $baseName = pathinfo($safeName, PATHINFO_FILENAME);

    // Create unique filename with user_id
    $finalName = $baseName . "_" . $studentId;
    $counter = 0;

    do {
        $suffix = $counter === 0 ? "" : "_($counter)";
        $newFileName = $finalName . $suffix . "." . $extension;
        $destPath = "$uploadDir/$newFileName";
        $counter++;
    } while (file_exists($destPath));

    // Save file and return relative path
    file_put_contents($destPath, base64_decode($fileData));
    $filePath = "/uploads/$newFileName";
}


            $startDate = $input['start_date'] ?? date('Y-m-d');
            $stmt = $conn->prepare("INSERT INTO assignment (subject_id, title, details, file_path, start_date, end_date, percentage_of_grade)
                VALUES (?, ?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("isssssd", $subject_id, $title, $details, $filePath, $startDate, $deadline, $percentage);
            $stmt->execute();

            $newTotal = $accumulated + $percentage;
            $stmt = $conn->prepare("UPDATE subject SET accumulated_percentage = ? WHERE subject_id = ?");
            $stmt->bind_param("di", $newTotal, $subject_id);
            $stmt->execute();

            echo json_encode(["success"=>true, "message"=>"Assignment added", "assignment_id"=> $conn->insert_id]);
            return;
        }

        // --- UPDATE ASSIGNMENT ---
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
            $stmt = $conn->prepare("UPDATE assignment SET subject_id=?, title=?, details=?, end_date=?, percentage_of_grade=? WHERE assignment_id=?");
            $stmt->bind_param("isssdi", $subject_id, $title, $details, $deadline, $percentage, $id);
            $stmt->execute();
            echo json_encode(["success"=>true, "message"=>"Assignment updated"]);
            return;
        }

        // --- DELETE ASSIGNMENT ---
        if ($input['mode'] === 'delete') {
            if (!isset($input['id'])) throw new Exception("Missing assignment ID for deletion");
            $id = intval($input['id']);
            $stmt = $conn->prepare("DELETE FROM assignment WHERE assignment_id = ?");
            $stmt->bind_param("i", $id);
            $stmt->execute();
            echo json_encode(["success"=>true, "message"=>"Assignment deleted"]);
            return;
        }
    }

    http_response_code(405);
    echo json_encode(["error"=>"Method not allowed"]);

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success"=>false, "error"=> $e->getMessage()]);
}