<?php
require_once "db.inc.php";
global $conn;

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

$method = $_SERVER['REQUEST_METHOD'];
$input = json_decode(file_get_contents("php://input"), true);

try {
    if ($method === 'GET') {
        if (isset($_GET['subject_id'])) {
            $sid = (int)$_GET['subject_id'];
            $stmt = $conn->prepare("
                SELECT s.subject_id, s.class_id, s.title, c.class_name
                FROM subject s
                JOIN class c ON s.class_id = c.class_id
                WHERE s.subject_id = ?
            ");
            $stmt->bind_param("i", $sid);
            $stmt->execute();
            $row = $stmt->get_result()->fetch_assoc();

            if (!$row) {
                http_response_code(404);
                echo json_encode(["error" => "Subject not found"]);
            } else {
                echo json_encode($row);
            }

        } elseif (isset($_GET['class_id'])) {
            $class_id = (int)$_GET['class_id'];
            $stmt = $conn->prepare("
                SELECT *
                FROM subject s
                WHERE s.class_id = ?
            ");
            $stmt->bind_param("i", $class_id);
            $stmt->execute();
            $result = $stmt->get_result();
            $subjects = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
            echo json_encode($subjects);
        } else {
            $sql = "
                SELECT s.subject_id, s.class_id, s.title, c.class_name
                FROM subject s
                JOIN class c ON s.class_id = c.class_id
            ";
            $result = $conn->query($sql);
            $subjects = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
            echo json_encode($subjects);
        }

    } elseif ($method === 'POST') {
        foreach (['title', 'class_id'] as $key) {
            if (empty($_POST[$key])) {
                throw new Exception("Missing field: $key");
            }
        }

        $title = $_POST['title'];
        $class_id = (int)$_POST['class_id'];

        $stmt = $conn->prepare("INSERT INTO subject (title, class_id) VALUES (?, ?)");
        $stmt->bind_param("si", $title, $class_id);
        $stmt->execute();

        http_response_code(201);
        echo json_encode([
            "success" => true,
            "message" => "Subject added",
            "subject_id" => $conn->insert_id
        ]);

    } elseif ($method === 'PUT') {
        if (empty($input['subject_id'])) {
            throw new Exception("Missing subject_id");
        }

        $subject_id = (int)$input['subject_id'];
        $title = $input['title'];
        $class_id = (int)$input['class_id'];

        $stmt = $conn->prepare("UPDATE subject SET title = ?, class_id = ? WHERE subject_id = ?");
        $stmt->bind_param("sii", $title, $class_id, $subject_id);
        $stmt->execute();

        echo json_encode([
            "success" => true,
            "message" => "Subject updated"
        ]);

    } elseif ($method === 'DELETE') {
        if (empty($_GET['subject_id'])) {
            throw new Exception("Missing subject_id");
        }

        $subject_id = (int)$_GET['subject_id'];

        $stmt = $conn->prepare("DELETE FROM subject WHERE subject_id = ?");
        $stmt->bind_param("i", $subject_id);
        $stmt->execute();

        echo json_encode([
            "success" => true,
            "message" => "Subject deleted"
        ]);

    } else {
        http_response_code(405);
        echo json_encode(["error" => "Method not allowed"]);
    }

} catch (Exception $ex) {
    http_response_code(400);
    echo json_encode([
        "success" => false,
        "error" => $ex->getMessage()
    ]);
}
?>
