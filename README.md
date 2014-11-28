Bukkit-плагин для блокировки предметов. Также позволяет запрещать взаимодействие с определёнными блоками в регионе WorldGuard.


Команды:

    /icy block [ignoremeta / nometa] [allowcraft / craft] - запретить использование предмета  
    /icy unblock [ignoremeta / nometa] - снова разрешить использование предмета    
    /icy protect [ignoremeta / nometa] - запретить использование блока в WorldGuard региона  
    /icy unprotect [ignoremeta / nometa] - снова разрешить использование блока в регионе WorldGuard 
    /icy <blocked / items / list> - вывести список запрещённых и защищенных предметов  
    
Параметры команд, вводятся в любом порядке:

    [ignoremeta / nometa] - игорировать метадату. Тоесть, при проверке совпадения метадата (повреждение) предмета не имеет значения.
    [allowcraft / craft] - разрешать крафт и хранение предмета.
    
Все команды, кроме последней, выполняются игроком с предметом в руке.

После удачной выполнении команды конфиг перезаписывается автоматически.

Права:

    icyblocker.add - /icy block; /icy protect
    icyblocker.remove - /icy unblock; /icy unprotect
    icyblocker.ignore - игнорировать запреты
