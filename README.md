# Huynh Toan Travel — Spring Boot 3.5 / MySQL / Thymeleaf

Starter architecture for a multi-service travel booking + transport dispatch platform focused on Đà Nẵng ↔ Hội An.

## Included
- MySQL DDL for users/roles/bookings_master/vehicle_bookings/ticket_bookings/hotels/rooms/room_bookings/payments + support tables.
- JPA entity relationships: BookingMaster → vehicles/tickets/rooms/payments.
- Spring Security form login. `/checkout/**` requires authentication, so Spring Security's saved request returns the user to checkout after login; session cart can remain intact.
- Anti-overbooking service using the overlap rule: `existing.checkIn < requested.checkOut AND existing.checkOut > requested.checkIn` and only active paid/deposited bookings block inventory.
- VietQR Quick Link builder with exact amount + booking code in `addInfo`.
- SePay and Casso webhook adapters, idempotency by provider + transaction id, exact amount verification, automatic booking state updates.
- Polling API: `GET /api/bookings/{code}/status`.
- JavaMailSender hooks for customer + admin notifications.
- ZXing QR helper and ticket QR token issuance after payment confirmation.
- Hero 3-slide Swiper template with autoplay 5s / fade / navigation / pagination and glassmorphism multi-service quick search.
- Hotel split-view Leaflet template: cards + markers + `flyTo` on hover + marker popups.

## Run
1. MySQL: execute `src/main/resources/db/schema.sql`.
2. Configure environment variables (or edit `application.yml`):
   - `DB_URL`, `DB_USER`, `DB_PASSWORD`
   - `MAIL_USERNAME`, `MAIL_PASSWORD`, `ADMIN_EMAIL`
   - `VIETQR_BANK_ID`, `VIETQR_ACCOUNT_NO`, `VIETQR_ACCOUNT_NAME`
   - `SEPAY_WEBHOOK_SECRET`, `CASSO_WEBHOOK_SECRET`
3. Run:
   ```bash
   mvn spring-boot:run
   ```
4. Open `http://localhost:8080`.

## Webhook endpoints
- `POST /api/webhooks/sepay`
- `POST /api/webhooks/casso`
- SePay: HMAC-SHA256 using `X-SePay-Signature` + `X-SePay-Timestamp` over `{timestamp}.{raw_body}`.
- Casso starter adapter: `X-Webhook-Secret: <your-secret>`; replace/map this to the authentication mode configured in your Casso webhook.

The SePay endpoint includes 5-minute replay-window validation, provider transaction deduplication, and exact amount verification.

## VietQR
This starter uses VietQR Quick Link:
`https://img.vietqr.io/image/<BANK_ID>-<ACCOUNT_NO>-<TEMPLATE>.png?amount=<AMOUNT>&addInfo=<BOOKING_CODE>&accountName=<ACCOUNT_NAME>`

## Important production hardening
- Replace synchronous email with an outbox/queue (RabbitMQ/Kafka/SQS) so webhook response is fast and retry-safe.
- Add a `webhook_events`/transaction log table if you want raw events independent of the `payments` row.
- Verify SePay HMAC and Casso authentication exactly from your account configuration.
- Lock payment row (`PESSIMISTIC_WRITE`) or use a unique event insert for race-safe multi-instance processing.
- Use Redis-backed cart/session if deploying more than one instance.
- Add inventory reservation TTL for unpaid hotel carts if you want short-term room holds before deposit.
- Build Tailwind assets locally for production instead of CDN.
- Store hotel images locally/CDN and replace demo Unsplash URLs.
- Add CSRF-protected POST endpoints for all cart and checkout mutations.
- Do not expose booking status publicly without ownership/auth checks in production.

## Business data still needed from owner
1. Receiving bank: bank/BIN, account number, exact account holder name.
2. Actual price tables for each route + car class + one-way/roundtrip/hour/day.
3. Bà Nà / Ký Ức Hội An price tables, local-resident rules, validity rules, and ticket provider integration (if any).
4. Hotel inventory model: one row per physical room vs room-type inventory count.
5. Deposit cancellation/refund/expiry policy.
6. SMTP sender address and admin notification recipients.
7. Brand logo and final hero photos.
