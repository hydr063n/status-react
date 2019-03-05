(ns status-im.ui.components.chat-icon.screen
  (:require [clojure.string :as string]
            [re-frame.core :as re-frame.core]
            [status-im.ui.components.chat-icon.styles :as styles]
            [status-im.ui.components.colors :as colors]
            [status-im.ui.components.react :as react]
            [status-im.ui.screens.chat.photos :as photos])
  (:require-macros [status-im.utils.views :refer [defview letsubs]]))

(defn default-chat-icon [name styles]
  (when-not (string/blank? name)
    [react/view (:default-chat-icon styles)
     [react/text {:style (:default-chat-icon-text styles)}
      ;; TODO: for now we check if the first letter is a #
      ;; which means it is most likely a public chat and
      ;; use the second letter if that is the case
      ;; a broader refactoring should clean up upstream params
      ;; for default-chat-icon
      (string/capitalize (if (and (= "#" (first name))
                                  (< 1 (count name)))
                           (second name)
                           (first name)))]]))

(defn dapp-badge [{:keys [online-view-wrapper online-view online-dot-left online-dot-right]}]
  [react/view online-view-wrapper
   [react/view online-view
    [react/view
     [react/view online-dot-left]
     [react/view online-dot-right]]]])

(defview pending-contact-badge
  [chat-id {:keys [pending-wrapper pending-outer-circle pending-inner-circle]}]
  (letsubs [pending-contact? [:get-in [:contacts/contacts chat-id :pending?]]]
    (when pending-contact?
      [react/view pending-wrapper
       [react/view pending-outer-circle
        [react/view pending-inner-circle]]])))

(defn chat-icon-view
  [chat-id _group-chat name _online styles & [hide-dapp?]]
  (let [photo-path (re-frame.core/subscribe [:contacts/chat-photo chat-id])
        dapp?      (re-frame.core/subscribe [:get-in [:contacts/contacts chat-id :dapp?]])]
    [react/view (:container styles)
     (if-not (string/blank? @photo-path)
       [photos/photo @photo-path styles]
       [default-chat-icon name styles])
     (when (and @dapp? (not hide-dapp?))
       [dapp-badge styles])
     [pending-contact-badge chat-id styles]]))

(defn chat-icon-view-toolbar [chat-id group-chat name color online]
  [chat-icon-view chat-id group-chat name online
   {:container              styles/container-chat-toolbar
    :online-view-wrapper    styles/online-view-wrapper
    :online-view            styles/online-view
    :online-dot-left        styles/online-dot-left
    :online-dot-right       styles/online-dot-right
    :pending-wrapper        styles/pending-wrapper
    :pending-outer-circle   styles/pending-outer-circle
    :pending-inner-circle   styles/pending-inner-circle
    :size                   36
    :chat-icon              styles/chat-icon-chat-toolbar
    :default-chat-icon      (styles/default-chat-icon-chat-toolbar color)
    :default-chat-icon-text styles/default-chat-icon-text}])

(defn chat-icon-view-chat-list [chat-id group-chat name color online & [hide-dapp?]]
  [chat-icon-view chat-id group-chat name online
   {:container              styles/container-chat-list
    :online-view-wrapper    styles/online-view-wrapper
    :online-view            styles/online-view
    :online-dot-left        styles/online-dot-left
    :online-dot-right       styles/online-dot-right
    :pending-wrapper        styles/pending-wrapper
    :pending-outer-circle   styles/pending-outer-circle
    :pending-inner-circle   styles/pending-inner-circle
    :size                   40
    :chat-icon              styles/chat-icon-chat-list
    :default-chat-icon      (styles/default-chat-icon-chat-list color)
    :default-chat-icon-text styles/default-chat-icon-text}
   hide-dapp?])

(defn chat-icon-view-action [chat-id group-chat name online]
  ^{:key chat-id}
  [chat-icon-view chat-id group-chat name online
   {:container              styles/container-chat-list
    :online-view-wrapper    styles/online-view-wrapper
    :online-view            styles/online-view
    :online-dot-left        styles/online-dot-left
    :online-dot-right       styles/online-dot-right
    :size                   40
    :chat-icon              styles/chat-icon-chat-list
    :default-chat-icon      (styles/default-chat-icon-chat-list colors/default-chat-color)
    :default-chat-icon-text styles/default-chat-icon-text}])

(defn chat-icon-view-menu-item [chat-id group-chat name color online]
  ^{:key chat-id}
  [chat-icon-view chat-id group-chat name online
   {:container              styles/container-menu-item
    :online-view-wrapper    styles/online-view-menu-wrapper
    :online-view            styles/online-view-menu-item
    :online-dot-left        styles/online-dot-left-menu-item
    :online-dot-right       styles/online-dot-right-menu-item
    :pending-wrapper        styles/pending-view-menu-wrapper
    :pending-outer-circle   styles/pending-outer-circle
    :pending-inner-circle   styles/pending-inner-circle
    :size                   24
    :chat-icon              styles/chat-icon-menu-item
    :default-chat-icon      (styles/default-chat-icon-view-action color)
    :default-chat-icon-text styles/default-chat-icon-text}
   true])

(defn chat-icon-message-status [chat-id group-chat name color online]
  ^{:key chat-id}
  [chat-icon-view chat-id group-chat name online
   {:container              styles/container-message-status
    :online-view-wrapper    styles/online-view-wrapper
    :online-view            styles/online-view
    :online-dot-left        styles/online-dot-left
    :online-dot-right       styles/online-dot-right
    :pending-wrapper        styles/pending-wrapper
    :pending-outer-circle   styles/pending-outer-circle
    :pending-inner-circle   styles/pending-inner-circle
    :size                   64
    :chat-icon              styles/chat-icon-message-status
    :default-chat-icon      (styles/default-chat-icon-message-status color)
    :default-chat-icon-text styles/message-status-icon-text}])

(defn contact-icon-view [{:keys [photo-path name dapp?]} {:keys [container] :as styles}]
  (let [photo-path photo-path]
    [react/view container
     (if-not (string/blank? photo-path)
       [photos/photo photo-path styles]
       [default-chat-icon name styles])
     (when dapp?
       [dapp-badge styles])]))

(defn contact-icon-contacts-tab [contact]
  [contact-icon-view contact
   {:container              styles/container-chat-list
    :online-view-wrapper    styles/online-view-wrapper
    :online-view            styles/online-view
    :online-dot-left        styles/online-dot-left
    :online-dot-right       styles/online-dot-right
    :size                   40
    :chat-icon              styles/chat-icon-chat-list
    :default-chat-icon      (styles/default-chat-icon-chat-list colors/default-chat-color)
    :default-chat-icon-text styles/default-chat-icon-text}])

(defn dapp-icon-browser [contact size]
  [contact-icon-view contact
   {:container              {:width size :height size :top 3 :margin-left 2}
    :online-view-wrapper    styles/online-view-wrapper
    :online-view            styles/online-view
    :online-dot-left        styles/online-dot-left
    :online-dot-right       styles/online-dot-right
    :size                   size
    :chat-icon              (styles/custom-size-icon size)
    :default-chat-icon      (styles/default-chat-icon-chat-list colors/default-chat-color)
    :default-chat-icon-text styles/default-chat-icon-text}])

(defn dapp-icon-permission [contact size]
  [contact-icon-view contact
   {:container              {:width size :height size}
    :online-view-wrapper    styles/online-view-wrapper
    :online-view            styles/online-view
    :online-dot-left        styles/online-dot-left
    :online-dot-right       styles/online-dot-right
    :size                   size
    :chat-icon              (styles/custom-size-icon size)
    :default-chat-icon      (styles/default-chat-icon-profile colors/default-chat-color size)
    :default-chat-icon-text styles/default-chat-icon-text}])

(defn profile-icon-view [photo-path name color edit? size override-styles]
  (let [styles (merge {:container              {:width size :height size}
                       :online-view            styles/online-view-profile
                       :online-dot-left        styles/online-dot-left-profile
                       :online-dot-right       styles/online-dot-right-profile
                       :size                   size
                       :chat-icon              styles/chat-icon-profile
                       :default-chat-icon      (styles/default-chat-icon-profile color size)
                       :default-chat-icon-text styles/default-chat-icon-text} override-styles)]
    [react/view (:container styles)
     (when edit?
       [react/view (styles/profile-icon-mask size)])
     (when edit?
       [react/view (styles/profile-icon-edit-text-containter size)
        [react/i18n-text {:style styles/profile-icon-edit-text :key :edit}]])
     (if (and photo-path (seq photo-path))
       [photos/photo photo-path styles]
       [default-chat-icon name styles])]))

(defn my-profile-icon [{{:keys [photo-path name]} :account
                        edit?                     :edit?}]
  (let [color colors/default-chat-color
        size  56]
    [profile-icon-view photo-path name color edit? size {}]))
