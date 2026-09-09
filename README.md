# Security Alarm System — Client/Server (Java)

A multi-client Java socket application simulating a security company's control
room system. A house alarm can be triggered by a client, routed through a
central control room server, and dispatched to a security guard — with the
guard's progress tracked back through the same server.

## Overview

The system is made up of three independent Java programs that communicate
over TCP sockets on **port 4321**:

| Program | Role | Type |
|---|---|---|
| `ControlRoomServer` | Central server — receives alarms, prompts the operator to dispatch a guard, relays guard status | Console app |
| `ClientHome` | House keypad — arms/disarms the alarm with a code sequence, reports intrusions | Swing GUI |
| `Guard` | Guard's device — polls the server every 3 seconds for a dispatch, walks through response stages | Swing GUI |

## How it works

1. **Arming/disarming** — `ClientHome` requires buttons `1`, `3`, `2` to be
   pressed in that exact sequence to toggle the alarm between armed and
   disarmed. Any wrong button resets the sequence.
2. **Triggering an alarm** — while armed, pressing "Move" or "Alarm" opens a
   new connection to `ControlRoomServer`, identifies as `"Home"`, and sends
   the house address.
3. **Dispatch decision** — the server prints the address and pops up a
   Yes/No/Cancel dialog asking the control room operator whether to send a
   guard.
4. **Guard polling** — `Guard` reconnects to the server every 3 seconds,
   identifies as `"Guard"`, and receives either the dispatched address or
   `"No alarms"`.
5. **Guard response sequence** — once dispatched, the guard progresses
   through:
   - **On my way** → sends `"Guard on his way"`
   - **Arrived** → sends `"Guard arrived"`
   - **Location save** (+ Send backup button appears) → sends `"House is save"`
     when confirming, or `"Send backup"` if backup is requested
6. **Reset** — once the server receives `"House is save"`, it clears the
   alarm state so the system is ready for the next alert.

## Tech stack

- Java (Sockets, `ServerSocket`/`Socket`, multithreading)
- Swing (`JFrame`, `JOptionPane`, `Timer`) for both client GUIs
- Client–server communication over plain-text TCP, one message per line

## Project structure

The repository has three folders, one per program: `ControlRoomServer`,
`ClientHome`, and `Guard`, each containing its Java source file. Alongside
these is a `screenshots` folder holding the server console output and both
GUI screenshots referenced below.

## Running it

Each program is a separate project — run them in this order:

1. Start `ControlRoomServer` first (it listens on port 4321).
2. Start `Guard` — it will begin polling immediately (shows "No alarms"
   until something is dispatched).
3. Start `ClientHome`, enter an address, arm the alarm (`1 → 3 → 2`), then
   press "Move" or "Alarm" to trigger a test alert.
4. Respond "Yes" in the server's dispatch popup, then watch the Guard
   window update through its response stages.

## Screenshots


**Server console — receiving an alarm and dispatch prompt**
![Server console](<img width="1516" height="928" alt="image" src="https://github.com/user-attachments/assets/abfaf53a-5479-4d56-85f2-78937de4a41a" />
)



**Home GUI — armed keypad**
![Home GUI](<img width="1477" height="863" alt="image" src="https://github.com/user-attachments/assets/09d985f4-2781-487d-b7f6-a26001fe3370" />

<img width="1442" height="853" alt="image" src="https://github.com/user-attachments/assets/1153b901-15a2-4f50-a515-d6a4f848323a" />

<img width="1482" height="833" alt="image" src="https://github.com/user-attachments/assets/1480d48e-bc6d-42d2-afd6-9a61bb968ddb" />
)



**Guard GUI — response sequence (on my way → arrived → location save → send backup)**
![Guard GUI](<img width="1510" height="930" alt="image" src="https://github.com/user-attachments/assets/ca91ece4-fc64-4bf8-970b-670cdab63da8" />

<img width="1512" height="932" alt="image" src="https://github.com/user-attachments/assets/c10d6a0a-2b6d-422c-b650-f0735ebc8785" />

<img width="1511" height="932" alt="image" src="https://github.com/user-attachments/assets/8e6a66a1-f562-4d9c-b245-b0c4ecf87b0f" />
)


