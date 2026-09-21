# Concurrency view

## The two cooks

The kitchen uses two threads. Each one runs a `Cook`.

`KitchenService` creates them in `start`, keeps them in a list, and waits for
them in `waitUntilDone`.

A cook takes one line of the order, sleeps for its preparation time, counts it
as finished, then takes the next one.

## How a cook stops

- The queue is empty, so the cook has nothing left to do and returns.
- `close` interrupts both cooks. A sleeping cook gets an `InterruptedException`,
  puts the flag back and returns. The other one sees the flag and returns too.

A cook cannot run forever, because the queue only gets smaller.

## What the cooks share

They share one `KitchenBoard`: the queue of lines to prepare, and the number of
finished lines.

Its three methods are `synchronized`, so only one cook is inside at a time.
Without that, two cooks could take the same line, or one count could be lost.

They do not share the status of the order. Only the main thread changes it.

## The join

`waitUntilDone` calls `join` on both cooks, then reads the number:

- all the lines are finished, the order becomes `READY`;
- some lines are missing, the order goes back to `CONFIRMED`.

We read the number after the join. No cook is running then, so it cannot change
any more.

The printed lines come in a different order at every run, so they prove nothing.
`KitchenTest` and `KitchenServiceTest` run the same case 10 times and check the
number and the status.
