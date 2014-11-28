package com.multi.icyblocker;

public class ItemData {

   private String name;
   private String meta;
   private String[] params;


   public ItemData(String name, String meta, String ... params) {
      this.name = name;
      this.meta = meta;
      this.params = params;
   }

   public void setParams(String[] params) {
      this.params = params;
   }

   public String getMeta() {
      return this.meta;
   }

   public String getName() {
      return this.name;
   }

   public boolean ignoresMeta() {
      return this.meta.equals("-1");
   }

   public boolean containsParam(String par) {
      if(this.params == null) {
         return false;
      } else {
         String[] arr = this.params;

          for (String p : arr) {
              if (p.equals(par)) {
                  return true;
              }
          }

         return false;
      }
   }

   public static ItemData create(String name, String meta, String ... params) {
      return new ItemData(name, meta, params);
   }

   public boolean equals(Object obj) {
      if(!(obj instanceof ItemData)) {
         return false;
      } else {
         ItemData item = (ItemData)obj;
         return item.getName().equals(this.getName()) && (this.ignoresMeta() || this.getMeta().equals(item.getMeta()));
      }
   }

   public String toString() {
      String s = this.name + ":" + this.getMeta();
      if(this.params != null) {
         String[] arr = this.params;
          for (String par : arr) {
              if (!par.trim().equals("")) {
                  s = s + ":" + par;
              }
          }
      }

      return s;
   }

   public static ItemData parse(String s) {
      if(!s.contains(":")) {
         return null;
      } else {
         String[] splitted = s.split(":");
         ItemData data = create(splitted[0], splitted[1]);
         if(splitted.length > 2) {
            String[] pars = new String[splitted.length - 2];
            System.arraycopy(splitted, 2, pars, 0, pars.length);
            data.setParams(pars);
         }

         return data;
      }
   }
}
