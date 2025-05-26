<?php
require_once "db.inc.php";
global $conn;

header("Content-Type: application/json; charset=utf-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

$method = $_SERVER['REQUEST_METHOD'];
$input = json_decode(file_get_contents("php://input"), true);

try {
    if ($method === 'GET') {
        if (isset($_GET['schedule_id'])) {
            $schedule_id = (int)$_GET['schedule_id'];
            $stmt = $conn->prepare("SELECT * FROM schedule_subject ss, subject s, class c WHERE ss.schedule_id = ? AND ss.subject_id = s.subject_id AND s.class_id = c.class_id");
            $stmt->bind_param("i", $schedule_id);
            $stmt->execute();
            $result = $stmt->get_result();
            $rows = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
            echo json_encode($rows);
        } else {
            $result = $conn->query("SELECT * FROM schedule_subject ss, subject s, class c WHERE ss.subject_id = s.subject_id AND s.class_id = c.class_id");
            $rows = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
            echo json_encode($rows);
        }

    } elseif ($method === 'POST') {
        foreach (["schedule_id", "subject_id", "day", "start_time", "end_time"] as $key) {
            if (empty($input[$key])) {
                throw new Exception("Missing field: $key");
            }
        }

        $stmt = $conn->prepare("
            INSERT INTO schedule_subject (schedule_id, subject_id, day, start_time, end_time)
            VALUES (?, ?, ?, ?, ?)
        ");
        $stmt->bind_param(
            "iisss",
            $input['schedule_id'],
            $input['subject_id'],
            $input['day'],
            $input['start_time'],
            $input['end_time']
        );
        $stmt->execute();

        echo json_encode(["success" => true, "message" => "Schedule subject added"]);

    } elseif ($method === 'PUT') {
        foreach (["schedule_id", "subject_id", "day", "start_time", "end_time", "old_schedule_id", "old_subject_id", "old_day"] as $key) {
            if (!isset($input[$key])) {
                throw new Exception("Missing field: $key");
            }
        }

        $stmt = $conn->prepare("
            UPDATE schedule_subject 
            SET schedule_id = ?, subject_id = ?, day = ?, start_time = ?, end_time = ?
            WHERE schedule_id = ? AND subject_id = ? AND day = ?
        ");
        $stmt->bind_param(
            "iisssiis",
            $input['schedule_id'],
            $input['subject_id'],
            $input['day'],
            $input['start_time'],
            $input['end_time'],
            $input['old_schedule_id'],
            $input['old_subject_id'],
            $input['old_day']
        );
        $stmt->execute();

        echo json_encode(["success" => true, "message" => "Schedule subject updated"]);

    } elseif ($method === 'DELETE') {
        foreach (["schedule_id", "subject_id", "day"] as $key) {
            if (empty($_GET[$key])) {
                throw new Exception("Missing field: $key");
            }
        }

        $stmt = $conn->prepare("
            DELETE FROM schedule_subject 
            WHERE schedule_id = ? AND subject_id = ? AND day = ?
        ");
        $stmt->bind_param(
            "iis",
            $_GET['schedule_id'],
            $_GET['subject_id'],
            $_GET['day']
        );
        $stmt->execute();

        echo json_encode(["success" => true, "message" => "Schedule subject deleted"]);

    } else {
        http_response_code(405);
        echo json_encode(["error" => "Method not allowed"]);
    }

} catch (Exception $e) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => $e->getMessage()
    ]);
}
