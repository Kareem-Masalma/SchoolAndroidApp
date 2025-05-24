<?php
header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once "db.inc.php";
$method = $_SERVER['REQUEST_METHOD'];
$input = json_decode(file_get_contents("php://input"), true);

try {
    if ($method === 'GET') {
        if (isset($_GET["user_id"])) {
            $uid = (int)$_GET["user_id"];
            $sql = "
                SELECT u.user_id, u.first_name, u.last_name, u.birth_date, u.address,
                       u.phone, u.role, t.speciality, t.schedule_id
                FROM users u
                JOIN teacher t ON t.user_id = u.user_id
                WHERE u.user_id = ?";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("i", $uid);
            $stmt->execute();
            $result = $stmt->get_result()->fetch_assoc();

            echo json_encode($result ?: ["error" => "Teacher not found"]);
        } else {
            $sql = "
                SELECT u.user_id, u.first_name, u.last_name, u.birth_date, u.address,
                       u.phone, u.role, t.speciality, t.schedule_id
                FROM users u
                JOIN teacher t ON t.user_id = u.user_id";
            $result = $conn->query($sql);
            echo json_encode($result ? $result->fetch_all(MYSQLI_ASSOC) : []);
        }

    } elseif ($method === 'POST') {
        foreach (['first_name', 'last_name', 'birth_date', 'address', 'phone', 'role', 'password', 'speciality'] as $key) {
            if (empty($input[$key])) {
                throw new Exception("Missing field: $key");
            }
        }

        $stmt = $conn->prepare("INSERT INTO users (first_name, last_name, birth_date, address, phone, role, password)
                                VALUES (?, ?, ?, ?, ?, ?, ?)");
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
        $user_id = $conn->insert_id;

        $schedule_id = $input['schedule_id'] ?? null;
        $stmt2 = $conn->prepare("INSERT INTO teacher (user_id, speciality, schedule_id)
                                 VALUES (?, ?, ?)");
        $stmt2->bind_param("isi", $user_id, $input['speciality'], $schedule_id);
        $stmt2->execute();

        echo json_encode(["success" => true, "message" => "Teacher added", "user_id" => $user_id]);

    } elseif ($method === 'PUT') {
        if (empty($input['user_id'])) {
            throw new Exception("Missing user_id");
        }

        $stmt = $conn->prepare("UPDATE users
                                SET first_name=?, last_name=?, birth_date=?, address=?, phone=?, role=?, password=?
                                WHERE user_id=?");
        $stmt->bind_param(
            "sssssssi",
            $input['first_name'],
            $input['last_name'],
            $input['birth_date'],
            $input['address'],
            $input['phone'],
            $input['role'],
            $input['password'],
            $input['user_id']
        );
        $stmt->execute();

        if (isset($input['speciality']) || isset($input['schedule_id'])) {
            $stmt2 = $conn->prepare("UPDATE teacher SET speciality=?, schedule_id=? WHERE user_id=?");
            $speciality = $input['speciality'] ?? null;
            $schedule_id = $input['schedule_id'] ?? null;
            $stmt2->bind_param("sii", $speciality, $schedule_id, $input['user_id']);
            $stmt2->execute();
        }

        echo json_encode(["success" => true, "message" => "Teacher updated"]);

    } elseif ($method === 'DELETE') {
        if (empty($_GET['user_id'])) {
            throw new Exception("Missing user_id");
        }

        $uid = (int)$_GET['user_id'];
        $conn->begin_transaction();

        $stmt1 = $conn->prepare("DELETE FROM teacher WHERE user_id=?");
        $stmt1->bind_param("i", $uid);
        $stmt1->execute();

        $stmt2 = $conn->prepare("DELETE FROM users WHERE user_id=?");
        $stmt2->bind_param("i", $uid);
        $stmt2->execute();

        $conn->commit();
        echo json_encode(["success" => true, "message" => "Teacher deleted"]);

    } else {
        http_response_code(405);
        echo json_encode(["error" => "Method not allowed"]);
    }

} catch (Exception $e) {
    if ($conn->in_transaction) $conn->rollback();
    http_response_code(400);
    echo json_encode(["success" => false, "error" => $e->getMessage()]);
}
?>
