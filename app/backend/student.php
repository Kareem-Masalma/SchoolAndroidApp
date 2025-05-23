<?php
header('Content-Type: application/json; charset=utf-8');
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
                u.password,
                s.class_id
              FROM users u
              JOIN student s ON s.user_id = u.user_id
              WHERE u.user_id = ?
            ";
      $stmt = $conn->prepare($sql);
      $stmt->bind_param('i', $uid);
      $stmt->execute();
      $row = $stmt->get_result()->fetch_assoc();
      echo $row
        ? json_encode($row)
        : json_encode(['error' => 'Student not found']);
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
                s.class_id
              FROM users u
              JOIN student s ON s.user_id = u.user_id
            ";
      $result = $conn->query($sql);
      $students = $result
        ? $result->fetch_all(MYSQLI_ASSOC)
        : [];
      echo json_encode($students);
    }
  } elseif ($method === 'POST') {
    // Required fields
    foreach (['first_name', 'last_name', 'birth_date', 'address', 'phone', 'role', 'password', 'class_id'] as $key) {
      if (empty($input[$key])) {
        throw new Exception("Missing field: $key");
      }
    }
    // Insert into users
    $stmt = $conn->prepare("
          INSERT INTO users
            (first_name, last_name, birth_date, address, phone, role, password)
          VALUES (?, ?, ?, ?, ?, ?)
        ");
    $stmt->bind_param(
      "ssssss",
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

    // Insert into student
    $stmt = $conn->prepare("
          INSERT INTO student (user_id, class_id)
          VALUES (?, ?)
        ");
    $stmt->bind_param("ii", $uid, $input['class_id']);
    $stmt->execute();

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

    // Update users
    $stmt = $conn->prepare("
          UPDATE users
            SET first_name = ?, last_name = ?, birth_date = ?, address = ?, phone = ?, role = ?, password = ?
          WHERE user_id = ?
        ");
    $stmt->bind_param(
      "ssssssi",
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

    // Optionally update class_id
    if (isset($input['class_id'])) {
      $stmt = $conn->prepare("
              UPDATE student
                SET class_id = ?
              WHERE user_id = ?
            ");
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

    // Delete from student first (FK)
    $stmt = $conn->prepare("DELETE FROM student WHERE user_id = ?");
    $stmt->bind_param("i", $uid);
    $stmt->execute();

    echo json_encode([
      'success' => true,
      'message' => 'Student deleted'
    ]);
  } else {
    http_response_code(405);
    echo json_encode(['error' => 'Method not allowed']);
  }
} catch (Exception $ex) {
  http_response_code(400);
  echo json_encode([
    'success' => false,
    'error'   => $ex->getMessage()
  ]);
}
