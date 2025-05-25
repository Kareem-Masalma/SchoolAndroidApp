<?php
global $conn;
require_once "db.inc.php";

// Headers
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

$method = $_SERVER['REQUEST_METHOD'];
$input  = json_decode(file_get_contents('php://input'), true);

try {
    if ($method === 'GET') {
        if (isset($_GET['user_id'])) {
            $uid = (int)$_GET['user_id'];
            $sql = "
        SELECT
          u.user_id,
          u.first_name,
          u.last_name,
          u.birth_date,
          u.address,
          u.phone,
          u.role,
          u.password,
          t.speciality
        FROM users u
        JOIN teacher t ON t.user_id = u.user_id
        WHERE u.user_id = ?
      ";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param('i', $uid);
            $stmt->execute();
            $row = $stmt->get_result()->fetch_assoc();

            if (!$row) {
                http_response_code(404);
                echo json_encode(['error' => 'Teacher not found']);
            } else {
                echo json_encode($row);
            }

        } else {
            $sql = "
        SELECT
          u.user_id,
          u.first_name,
          u.last_name,
          u.birth_date,
          u.address,
          u.phone,
          u.role,
          u.password,
          t.speciality
        FROM users u
        JOIN teacher t ON t.user_id = u.user_id
      ";
            $result = $conn->query($sql);
            $teachers = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
            echo json_encode($teachers);
        }

    } elseif ($method === 'POST') {
        foreach (['first_name', 'last_name', 'birth_date', 'address', 'phone', 'role', 'password', 'speciality'] as $key) {
            if (empty($input[$key])) {
                throw new Exception("Missing field: $key");
            }
        }

        // Insert into users
        $stmt = $conn->prepare("
      INSERT INTO users (first_name, last_name, birth_date, address, phone, role, password)
      VALUES (?, ?, ?, ?, ?, ?, ?)
    ");
        $stmt->bind_param(
            "sssssss",
            $input['first_name'],
            $input['last_name'],
            $input['birth_date'],
            $input['address'],
            $input['phone'],
            $input['role'],
            $input['password']
        );
        $stmt->execute();
        $uid = $conn->insert_id;

        // Insert into teacher
        $stmt = $conn->prepare("INSERT INTO teacher (user_id, speciality) VALUES (?, ?)");
        $stmt->bind_param("is", $uid, $input['speciality']);
        $stmt->execute();

        http_response_code(201);
        echo json_encode([
            'success' => true,
            'message' => 'Teacher added',
            'user_id' => $uid
        ]);

    } elseif ($method === 'PUT') {
        if (empty($input['user_id'])) {
            throw new Exception("Missing user_id");
        }
        $uid = (int)$input['user_id'];

        // Update users
        $stmt = $conn->prepare("
      UPDATE users
      SET first_name = ?, last_name = ?, birth_date = ?, address = ?, phone = ?, role = ?, password = ?
      WHERE user_id = ?
    ");
        $stmt->bind_param(
            "sssssssi",
            $input['first_name'],
            $input['last_name'],
            $input['birth_date'],
            $input['address'],
            $input['phone'],
            $input['role'],
            $input['password'],
            $uid
        );
        $stmt->execute();

        // Update teacher speciality
        if (isset($input['speciality'])) {
            $stmt = $conn->prepare("UPDATE teacher SET speciality = ? WHERE user_id = ?");
            $stmt->bind_param("si", $input['speciality'], $uid);
            $stmt->execute();
        }

        echo json_encode([
            'success' => true,
            'message' => 'Teacher updated'
        ]);

    } elseif ($method === 'DELETE') {
        if (empty($_GET['user_id'])) {
            throw new Exception("Missing user_id");
        }
        $uid = (int)$_GET['user_id'];

        $conn->begin_transaction();

        $stmt = $conn->prepare("DELETE FROM teacher WHERE user_id = ?");
        $stmt->bind_param("i", $uid);
        $stmt->execute();

        $stmt = $conn->prepare("DELETE FROM users WHERE user_id = ?");
        $stmt->bind_param("i", $uid);
        $stmt->execute();

        $conn->commit();

        echo json_encode([
            'success' => true,
            'message' => 'Teacher deleted'
        ]);

    } else {
        http_response_code(405);
        echo json_encode(['error' => 'Method not allowed']);
    }

} catch (Exception $ex) {
    if ($conn->in_transaction) {
        $conn->rollback();
    }
    http_response_code(400);
    echo json_encode([
        'success' => false,
        'error'   => $ex->getMessage()
    ]);
}
?>
