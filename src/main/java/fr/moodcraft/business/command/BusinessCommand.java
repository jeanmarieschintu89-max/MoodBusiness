package fr.moodcraft.business.command;

import fr.moodcraft.business.gui.*;
import fr.moodcraft.business.listener.RecruitmentChatListener;
import fr.moodcraft.business.manager.*;
import fr.moodcraft.business.model.*;
import fr.moodcraft.business.storage.BusinessStorage;
import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;

public class BusinessCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Commande joueur uniquement."); return true; }
        if (args.length == 0) { BusinessMainGUI.open(p); return true; }
        String sub = args[0].toLowerCase(Locale.ROOT);

        if (sub.equals("accepter") || sub.equals("accept")) return RecruitmentChatListener.acceptInvite(p);
        if (sub.equals("refuser") || sub.equals("refuse") || sub.equals("decline")) return RecruitmentChatListener.refuseInvite(p);
        if (sub.equals("alertes") || sub.equals("alerts") || sub.equals("notifications")) { AlertManager.showAlertHistory(p); return true; }
        if (sub.equals("staff")) { if (!p.hasPermission("moodbusiness.staff")) { BusinessMessages.deny(p,"Gestion Entreprises","Accès réservé au staff."); return true; } BusinessStaffGUI.open(p); return true; }

        if (sub.equals("creer") || sub.equals("créer") || sub.equals("create")) {
            if (args.length < 2) { helpCreate(p); return true; }
            BusinessManager.CreationResult result = BusinessManager.createBusiness(p, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            if (!result.success()) { BusinessMessages.deny(p,"Bureau des Entreprises",result.message()); return true; }
            Business b = result.business();
            BusinessMessages.header(p,"Bureau des Entreprises");
            p.sendMessage("§a✔ §fEntreprise créée."); p.sendMessage(""); p.sendMessage("§7Nom: §e" + b.getName()); p.sendMessage("§7Frais: §e" + BusinessMessages.money(b.getCreationFee())); p.sendMessage("§7État: " + b.getStatus().getDisplayName()); p.sendMessage(""); BusinessMessages.line(p,"Dossier inscrit au Bureau des Entreprises"); BusinessMessages.footer(p);
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE,0.8f,1.1f); BusinessMainGUI.open(p); return true;
        }

        if (sub.equals("info")) { Business b = args.length >= 2 ? BusinessManager.getByName(String.join(" ", Arrays.copyOfRange(args,1,args.length))) : BusinessManager.getOwnedBusiness(p.getUniqueId()); if (b == null) BusinessMessages.deny(p,"Dossier Entreprise","Aucune entreprise trouvée."); else BusinessMessages.businessInfo(p,b); return true; }
        if (sub.equals("employes") || sub.equals("employés") || sub.equals("employees") || sub.equals("equipe") || sub.equals("équipe")) { Business b = requireBusiness(p,"Équipe Entreprise"); if (b != null) BusinessEmployeesGUI.open(p,b); return true; }
        if (sub.equals("banque") || sub.equals("bank") || sub.equals("argent")) { Business b = requireBusiness(p,"Argent Entreprise"); if (b != null) BusinessBankGUI.open(p,b); return true; }

        if (sub.equals("depot") || sub.equals("dépôt") || sub.equals("deposit")) { Business b = requireBusiness(p,"Argent Entreprise"); if (b == null) return true; if (args.length < 2) { BusinessMessages.deny(p,"Argent Entreprise","Montant manquant."); return true; } replyBank(p, BusinessBankManager.deposit(p,b,parseDouble(args[1]))); return true; }
        if (sub.equals("retrait") || sub.equals("withdraw")) { Business b = requireBusiness(p,"Argent Entreprise"); if (b == null) return true; if (args.length < 2) { BusinessMessages.deny(p,"Argent Entreprise","Montant manquant."); return true; } replyBank(p, BusinessBankManager.withdraw(p,b,parseDouble(args[1]))); return true; }
        if (sub.equals("prime") || sub.equals("bonus")) { Business b = requireBusiness(p,"Argent Entreprise"); if (b == null) return true; if (args.length < 3) { BusinessMessages.deny(p,"Argent Entreprise","Indiquez le joueur et le montant."); return true; } UUID u = BusinessManager.getMemberUuidByName(b,args[1]); boolean confirmed = args.length >= 4 && args[3].equalsIgnoreCase("confirmer"); replyBank(p, BusinessBankManager.bonus(p,b,u,parseDouble(args[2]),confirmed)); return true; }

        if (sub.equals("paye") || sub.equals("paie") || sub.equals("pay")) { return handlePay(p,args); }
        if (sub.equals("recruter") || sub.equals("inviter") || sub.equals("hire")) { Business b = requireBusiness(p,"Équipe Entreprise"); if (b != null) RecruitmentChatListener.start(p,b); return true; }
        if (sub.equals("role") || sub.equals("rang")) { return handleRole(p,args); }
        if (sub.equals("renvoyer") || sub.equals("kick") || sub.equals("retirer")) { return handleRemove(p,args); }
        if (sub.equals("dissoudre") || sub.equals("dissolution") || sub.equals("supprimer") || sub.equals("delete") || sub.equals("fermer")) { Business b = requireBusiness(p,"Fermer Entreprise"); if (b == null) return true; if (!b.isOwner(p.getUniqueId())) { BusinessMessages.deny(p,"Fermer Entreprise","Seul le dirigeant peut fermer l'entreprise."); return true; } BusinessDissolveConfirmGUI.open(p,b); return true; }
        if (sub.equals("suspendre")) return staffStatus(p,args,BusinessStatus.SUSPENDUE);
        if (sub.equals("reactiver") || sub.equals("réactiver")) return staffStatus(p,args,BusinessStatus.ACTIVE);

        BusinessMainGUI.open(p); return true;
    }

    private boolean handlePay(Player p, String[] args) {
        Business b = requireBusiness(p,"Équipe Entreprise"); if (b == null) return true;
        if (!BusinessManager.canManageRoles(p,b)) { BusinessMessages.deny(p,"Équipe Entreprise","Votre rôle ne permet pas de modifier la paye."); return true; }
        if (args.length < 3) { BusinessMessages.deny(p,"Équipe Entreprise","Utilisation : §e/entreprise paye <joueur> <montant>"); return true; }
        UUID u = BusinessManager.getMemberUuidByName(b,args[1]);
        if (u == null || !b.isMember(u)) { BusinessMessages.deny(p,"Équipe Entreprise","Ce joueur n'est pas dans votre entreprise."); return true; }
        if (b.isOwner(u)) { BusinessMessages.deny(p,"Équipe Entreprise","La paye du dirigeant ne se configure pas ici."); return true; }
        double amount = parseDouble(args[2]); if (amount < 0) { BusinessMessages.deny(p,"Équipe Entreprise","Le montant ne peut pas être négatif."); return true; }
        b.setMemberPay(u, amount); BusinessStorage.save();
        BusinessMessages.success(p,"Équipe Entreprise","Paye mise à jour pour §e" + b.getMemberName(u) + "§f.","§8• §7Montant prévu : §e" + VaultHook.format(amount)); return true;
    }

    private boolean handleRole(Player p, String[] args) {
        Business b = requireBusiness(p,"Rôles Entreprise"); if (b == null) return true;
        if (args.length < 3) { BusinessMessages.deny(p,"Rôles Entreprise","Indiquez le joueur et le rôle."); return true; }
        UUID u = BusinessManager.getMemberUuidByName(b,args[1]); if (u == null) { BusinessMessages.deny(p,"Rôles Entreprise","Ce joueur n'est pas dans votre entreprise."); return true; }
        BusinessRole role = BusinessRole.fromText(args[2]); if (role == null) { BusinessMessages.deny(p,"Rôles Entreprise","Rôle inconnu."); return true; }
        BusinessManager.ActionResult result = BusinessManager.assignRole(p,b,u,role); if (!result.success()) BusinessMessages.deny(p,"Rôles Entreprise",result.message()); else BusinessMessages.success(p,"Rôles Entreprise",result.message()); return true;
    }

    private boolean handleRemove(Player p, String[] args) {
        Business b = requireBusiness(p,"Équipe Entreprise"); if (b == null) return true;
        if (args.length < 2) { BusinessMessages.deny(p,"Équipe Entreprise","Indiquez le joueur à licencier."); return true; }
        UUID u = BusinessManager.getMemberUuidByName(b,args[1]); if (u == null) { BusinessMessages.deny(p,"Équipe Entreprise","Ce joueur n'est pas dans votre entreprise."); return true; }
        BusinessManager.ActionResult result = BusinessManager.removeMember(p,b,u); if (!result.success()) BusinessMessages.deny(p,"Équipe Entreprise",result.message()); else BusinessMessages.success(p,"Équipe Entreprise",result.message()); return true;
    }

    private boolean staffStatus(Player p, String[] args, BusinessStatus status) {
        if (!p.hasPermission("moodbusiness.staff.suspend")) { BusinessMessages.deny(p,"Gestion Entreprises","Action réservée au staff."); return true; }
        if (args.length < 2) { BusinessMessages.deny(p,"Gestion Entreprises","Indiquez l'entreprise."); return true; }
        Business b = BusinessManager.getByName(String.join(" ", Arrays.copyOfRange(args,1,args.length))); if (b == null) { BusinessMessages.deny(p,"Gestion Entreprises","Entreprise introuvable."); return true; }
        BusinessManager.setStatus(b,status); AuditLogManager.log(status == BusinessStatus.ACTIVE ? AuditLogType.BUSINESS_REACTIVATED : AuditLogType.BUSINESS_SUSPENDED,p,b.getName(),b,"Statut modifié par l'administration."); BusinessMessages.success(p,"Gestion Entreprises","Statut mis à jour: §e" + b.getName()); return true;
    }

    private Business requireBusiness(Player p, String title) { Business b = BusinessManager.getMemberBusiness(p.getUniqueId()); if (b == null) BusinessMessages.deny(p,title,"Vous n'appartenez à aucune entreprise active."); return b; }
    private void replyBank(Player p, BusinessBankManager.BankResult result) { if (!result.success()) BusinessMessages.deny(p,"Argent Entreprise",result.message()); else BusinessMessages.success(p,"Argent Entreprise",result.message()); }
    private void helpCreate(Player p) { BusinessMessages.header(p,"Bureau des Entreprises"); p.sendMessage("§fCréer une entreprise."); p.sendMessage(""); p.sendMessage("§7Utilisation:"); p.sendMessage("§e/entreprise creer <nom>"); p.sendMessage(""); p.sendMessage("§8• §7Exemple: §e/entreprise creer NordBuild"); BusinessMessages.footer(p); }
    private double parseDouble(String text) { try { return Double.parseDouble(text.replace(",",".")); } catch (Exception e) { return -1; } }
}
