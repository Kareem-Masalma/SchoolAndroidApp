<?php
// user.php

global $conn;
header('Content-Type: application/json; charset=utf-8');
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
      $sql = "SELECT user_id, first_name, last_name, birth_date, address, phone, role FROM users WHERE user_id = ?";
      $stmt = $conn->prepare($sql);
      $stmt->bind_param('i', $uid);
      $stmt->execute();
      $row = $stmt->get_result()->fetch_assoc();

      if (!$row) {
        http_response_code(404);
        echo json_encode(['error' => 'User not found']);
      } else {
        echo json_encode($row);
      }
    } else {
      $sql = "SELECT user_id, first_name, last_name, birth_date, address, phone, role FROM users";
      $result = $conn->query($sql);
      $users = $result ? $result->fetch_all(MYSQLI_ASSOC) : [];
      echo json_encode($users);
    }
  } elseif ($method === 'POST') {
    // Required fields
    foreach (['first_name', 'last_name', 'birth_date', 'address', 'phone', 'role', 'password'] as $key) {
      if (empty($input[$key])) {
        throw new Exception("Missing field: $key");
      }
    }

    // Plaintext password
    $password = $input['password'];

    // Insert into users
    $stmt = $conn->prepare(
      "INSERT INTO users (first_name, last_name, birth_date, address, phone, role, password) VALUES (?, ?, ?, ?, ?, ?, ?)"
    );
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

    http_response_code(201);
    echo json_encode([
      'success' => true,
      'message' => 'User added',
      'user_id' => $uid
    ]);
  } elseif ($method === 'PUT') {
    if (empty($input['user_id'])) {
      throw new Exception("Missing user_id");
    }
    $uid = (int)$input['user_id'];

    // Plaintext password
    $password = $input['password'];

    // Update users
    $stmt = $conn->prepare(
      "UPDATE users SET first_name = ?, last_name = ?, birth_date = ?, address = ?, phone = ?, role = ?, password = ? WHERE user_id = ?"
    );
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

    echo json_encode([
      'success' => true,
      'message' => 'User updated'
    ]);
  } elseif ($method === 'DELETE') {
    if (empty($_GET['user_id'])) {
      throw new Exception("Missing user_id");
    }
    $uid = (int)$_GET['user_id'];

    $stmt = $conn->prepare("DELETE FROM users WHERE user_id = ?");
    $stmt->bind_param("i", $uid);
    $stmt->execute();

    echo json_encode([
      'success' => true,
      'message' => 'User deleted'
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
