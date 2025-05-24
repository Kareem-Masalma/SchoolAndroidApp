<?php
global $conn;
header('Content-Type: application/json; charset=utf-8');
// CORS (adjust origin as needed)
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type');

require_once "db.inc.php";

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
          s.class_id
        FROM users u
        JOIN student s ON s.user_id = u.user_id
        WHERE u.user_id = ?
      ";
      $stmt = $conn->prepare($sql);
      $stmt->bind_param('i', $uid);
      $stmt->execute();
      $row = $stmt->get_result()->fetch_assoc();

      if (!$row) {
        http_response_code(404);
        echo json_encode(['error' => 'Student not found']);
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
          s.class_id
        FROM users u
        JOIN student s ON s.user_id = u.user_id
      ";
      $result = $conn->query($sql);
      $students = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
      echo json_encode($students);
    }

  } elseif ($method === 'POST') {
    // Required fields
    foreach (['first_name', 'last_name', 'birth_date', 'address', 'phone', 'role', 'password', 'class_id'] as $key) {
      if (empty($input[$key])) {
        throw new Exception("Missing field: $key");
      }
    }

    // Use plaintext password (not hashing)
    $password = $input['password'];

    // Insert into users
    $stmt = $conn->prepare(
      "INSERT INTO users
         (first_name, last_name, birth_date, address, phone, role, password)
       VALUES (?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param(
      "sssssss",
      $input['first_name'],
      $input['last_name'],
      $input['birth_date'],
      $input['address'],
      $input['phone'],
      $input['role'],
      $password
    );
    $stmt->execute();
    $uid = $conn->insert_id;

    // Insert into student
    $stmt = $conn->prepare("INSERT INTO student (user_id, class_id) VALUES (?, ?)");
    $stmt->bind_param("ii", $uid, $input['class_id']);
    $stmt->execute();

    http_response_code(201);
    echo json_encode([
      'success' => true,
      'message' => 'Student added',
      'user_id' => $uid
    ]);

  } elseif ($method === 'PUT') {
    if (empty($input['user_id'])) {
      throw new Exception("Missing user_id");
    }
    $uid = (int)$input['user_id'];

    // Use plaintext password (not hashing)
    $password = $input['password'];

    // Update users
    $stmt = $conn->prepare(
      "UPDATE users
         SET first_name = ?, last_name = ?, birth_date = ?, address = ?, phone = ?, role = ?, password = ?
       WHERE user_id = ?");
    $stmt->bind_param(
      "sssssssi",
      $input['first_name'],
      $input['last_name'],
      $input['birth_date'],
      $input['address'],
      $input['phone'],
      $input['role'],
      $password,
      $uid
    );
    $stmt->execute();

    // Optionally update class_id
    if (isset($input['class_id'])) {
      $stmt = $conn->prepare("UPDATE student SET class_id = ? WHERE user_id = ?");
      $stmt->bind_param("ii", $input['class_id'], $uid);
      $stmt->execute();
    }

    echo json_encode([
      'success' => true,
      'message' => 'Student updated'
    ]);

  } elseif ($method === 'DELETE') {
    if (empty($_GET['user_id'])) {
      throw new Exception("Missing user_id");
    }
    $uid = (int)$_GET['user_id'];

    // Begin transaction
    $conn->begin_transaction();

    // Delete from student first (FK constraint)
    $stmt = $conn->prepare("DELETE FROM student WHERE user_id = ?");
    $stmt->bind_param("i", $uid);
    $stmt->execute();

    // Delete from users
    $stmt = $conn->prepare("DELETE FROM users WHERE user_id = ?");
    $stmt->bind_param("i", $uid);
    $stmt->execute();

    $conn->commit();

    echo json_encode([
      'success' => true,
      'message' => 'Student deleted'
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
