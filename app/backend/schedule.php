<?php
global $conn;
header("Content-Type: application/json");
require_once("db.inc.php");

$method = $_SERVER['REQUEST_METHOD'];
$input = json_decode(file_get_contents("php://input"), true);

try {
    if ($method === 'GET') {
        if (isset($_GET['schedule_id'])) {
            $schedule_id = $_GET['schedule_id'];
            $stmt = $conn->prepare("SELECT * FROM schedule WHERE schedule_id = ?");
            $stmt->bind_param("i", $schedule_id);
            $stmt->execute();
            $result = $stmt->get_result();
            $schedule = $result->fetch_assoc();
            echo json_encode($schedule);
        } else {
            $result = $conn->query("SELECT * FROM schedule");
            $schedules = [];
            while ($row = $result->fetch_assoc()) {
                $schedules[] = $row;
            }
            echo json_encode($schedules);
        }

    } elseif ($method === 'POST') {
        $input = json_decode(file_get_contents("php://input"), true);

        if (empty($input['user_id']) && empty($input['class_id'])) {
            echo json_encode(["success" => false, "message" => "Missing user_id or class_id"]);
            exit;
        }

        $stmt = $conn->prepare("INSERT INTO schedule () VALUES ()");
        $stmt->execute();
        $newId = $conn->insert_id;

        if (!empty($input['user_id'])) {
            $stmt = $conn->prepare("UPDATE teacher SET schedule_id = ? WHERE user_id = ?");
            $stmt->bind_param("ii", $newId, $input['user_id']);
            $stmt->execute();
        }

        if (!empty($input['class_id'])) {
            $stmt = $conn->prepare("UPDATE class SET schedule_id = ? WHERE class_id = ?");
            $stmt->bind_param("ii", $newId, $input['class_id']);
            $stmt->execute();
        }

        echo json_encode(["success" => true, "schedule_id" => $newId]);

    } elseif ($method === 'DELETE') {
        if (!isset($_GET['schedule_id'])) {
            throw new Exception("Missing schedule_id for deletion");
        }

        $schedule_id = $_GET['schedule_id'];
        $stmt = $conn->prepare("DELETE FROM schedule WHERE schedule_id = ?");
        $stmt->bind_param("i", $schedule_id);
        $stmt->execute();

        echo json_encode(["success" => true, "message" => "Schedule deleted"]);

    } else {
        http_response_code(405);
        echo json_encode(["error" => "Method not allowed"]);
    }
} catch (Exception $e) {
    http_response_code(400);
    echo json_encode(["success" => false, "error" => $e->getMessage()]);
}

