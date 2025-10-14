[![CI](https://github.com/mdht-daiki/coding-challenge-pws/actions/workflows/ci.yml/badge.svg)](https://github.com/mdht-daiki/coding-challenge-pws/actions/workflows/ci.yml)

# Procurement Workflow System (PWS)

公共団体向けの購買ワークフロー管理システム。  
Spring Boot 3 + Java 17 + PostgreSQL + Liquibase + Audit Trail（ハッシュ連鎖＋WORM）構成。

---

## 🚀 技術スタック

| 技術                | バージョン | 用途                           |
|-------------------|-------|------------------------------|
| Java              | 17    | メインランタイム                     |
| Spring Boot       | 3.x   | Web / JPA / Validation / AOP |
| PostgreSQL        | 15    | メインDB                        |
| Liquibase         | 最新    | スキーマ管理                       |
| JUnit5 + MockMvc  | -     | APIテスト                       |
| Springdoc OpenAPI | 2.8.x | API UI・ドキュメント                |
| Docker（任意）        | -     | DB / App 起動用（開発予定）           |

---

## 📂 プロジェクト構成（主要）

```

src/
└── main/
├── java/com/example/procurement
│    ├── domain/         # Entity類
│    ├── repository/     # JPA Repository
│    ├── service/        # Business logic
│    ├── web/            # Controller + DTO
│    └── audit/          # Audit logging
└── resources/
├── application.yml                     # dev用
└── db/changelog/
├── 001-...                         # 初期DDL
├── 002-...                         # auditログ + prevent update
├── 003-dev-seed-users.xml          # 開発用シード
└── changelog-master.xml

````

---

## 🔧 開発環境セットアップ

### 1. PostgreSQL (Docker)

```bash
docker compose up -d pws-postgres
# or 手動でPostgreSQLを5433ポートに配置
````

接続情報（application.yml）:

```
jdbc:postgresql://localhost:5433/pws
user: pws / pass: pws
```

### 2. アプリ起動

* IntelliJ → `PwsApplication.java` を実行
* または：`mvn spring-boot:run`

DB起動済みであれば、Liquibaseにより自動でテーブルとシードユーザーが作成されます。

---

## ✅ Quick Test

### 購買申請の作成

```bash
curl -s -X POST "http://localhost:8080/api/requests" \
 -H "Content-Type: application/json" \
 -H "X-Actor-Id: 22222222-2222-2222-2222-222222222222" \
 -d '{
   "applicantId":"22222222-2222-2222-2222-222222222222",
   "items":[{"skuId":"A-001","qty":2,"price":1000}],
   "totalAmount":2000
 }'
```

### 承認API（PATCH）

単段承認用のAPIです（現時点では SUBMITTED → APPROVED のみ許可）。

#### 正常系

```bash
curl -s -X PATCH "http://localhost:8080/api/requests/<REQ_ID>/approve" \
 -H "Content-Type: application/json" \
 -H "X-Actor-Id: 22222222-2222-2222-2222-222222222222" \
 -d '{"comment":"OK"}'
```

→ `{"status":"APPROVED"}` が返る

#### 再承認（異常系）

```bash
curl -i -X PATCH "http://localhost:8080/api/requests/<REQ_ID>/approve" \
 -H "Content-Type: application/json" \
 -H "X-Actor-Id: 22222222-2222-2222-2222-222222222222" \
 -d '{"comment":"2nd"}'
```

→ `HTTP/1.1 409 Conflict` + JSONエラー

---

## 🔍 OpenAPI / Swagger UI

| 種別                | URL                                           |
|-------------------|-----------------------------------------------|
| API定義(JSON)       | `http://localhost:8080/v3/api-docs`           |
| API UI（Scalar UI） | `http://localhost:8080/scalar`                |
| Swagger UI (ある場合) | `http://localhost:8080/swagger-ui/index.html` |

> ※ Spring Boot 3.5.x + Springdoc 2.8.x系では `/scalar` が標準UIになります。

---

## 🎯 プロファイル

| プロファイル          | 目的          | 説明                                      |
|-----------------|-------------|-----------------------------------------|
| `dev` (default) | 開発用         | PostgreSQL(5433)に接続、Liquibase実行、シード投入   |
| `test`          | CI・MockMvc用 | `DataSource` / JPA / Liquibase 自動設定を無効化 |

Test実行:

```bash
mvn verify -Dspring.profiles.active=test
```

---

## 🧪 テスト

* MockMvc により Controller 層のAPIテスト
* CI（GitHub Actions）で `mvn verify` 自動実行
* CodeQL によるセキュリティスキャン導入済み

---

## 🚧 今後の予定（TODO）

* 承認段数 / 金額閾値ロジックの決定と実装
* 差戻しAPI（REJECT / RETURN）の追加
* 会計CSVエクスポートAPI / エラーファイル分割の詳細設計
* Docker/Jib によるアプリコンテナ化（PR予定）
* 権限マトリクス最終確定とAccessControl実装
* WORM書き込みのmanifest.json設計

---

## 📜 監査ログ仕様（概要）

* `audit_log` テーブルは **append-only**
* `prev_hash` によりハッシュ連鎖を保持 (`SHA-256`)
* `worm_manifest` にS3 Object Lock用のメタデータを記録（将来拡張）

---

## 🌐 CI / Code Quality

* GitHub Actionsで `CI (mvn verify)`
* `CodeQL` による脆弱性スキャン
* `Surefire Reports` をアーティファクトとして保存

---

## 📄 ライセンス（公共案件のため検討中）

> 今後、公開範囲／二次配布方針が決まり次第明記予定

---


